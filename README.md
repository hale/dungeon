## Modified files:

```
src/dungeon/ai/hale
├── CreatureBehaviour.java
├── FactionBehaviour.java
└── pathfind
    ├── AStar.java
    ├── Grid.java
    └── Square.java

1 directory, 5 files
```

## Usage

To run from the command line:

    mvn compile exec:java
 
 To create a Jar:
 
     mvn compile package
    
To add to the base dungeon package, copy `/src/dungeon/ai/hale`  into `src/dungeon/ai`

## Requirements

Depends on Java 6+.  Running from the command line requires Maven.

## Maps

The Faction and Creature Behaviour attributes should be set like so:

    [...]
    <Factions>
      <Vector>
        <Faction Behaviour="dungeon.ai.hale.FactionBehaviour" [...]/>
        [...]
      </Vector>
    </Factions>
    <Creatures>
      <Vector>
        <Orc Behaviour="dungeon.ai.hale.CreatureBehaviour" [...]/>
        [...]
      </Vector>

---

## Strategy

It's worth briefly mentioning that the strategy for my faction has changed repeatedly in the past fortnight. This is because either the strategy behaved unexpectedly in situations I haven't considered, or because its implementation proved more complicated than I'd hoped.

Rather than explain all of the iterations this package has gone through, I will describe the current strategy and explain why it was chosen.

---

I wanted behaviour that could cope with any imaginable unknown map and any reasonable number of mobs and factions.

More specifically, the AI would have to be resilient to:

* Corridors with a dead end.
* Goals that change on each tick.
* Areas of the dungeon which are only accessible through a flame trap.
* Pits on the tile.
* When no path can be found.

I created a map which tests the above conditions.  

At first I was choosing a leader based on their current health.  Whilst this chose an appropriate leader, the faction would get stuck in dead-ends, as the followers would be blocking the leader.

Detecting for situations like that and fixing them retroactively is quite hard.  You might specify the condition as being 

> When two or more faction creatures are within roughly the same area, and they have a goal which hasn't been reached in the past few ticks, then switch leaders".  

Whilst this might work, you are still faced with the difficulties of:

* Determining 'the same area'.  In narrow corridors with lots of corners, you need pathfinding to be certain.
* Once you've determined faction creatures are too close, you still need to make sure they've been too close for a period of time - otherwise, they might just be moving in parallel.  It's possible to change this condition to "if the path of one creature contains another, switch leaders", but this can cause paths to be incorrectly discounted.
* No obvious criteria with which to pick the leader.

At this point, I wanted to write the behaviour in such a way that I didn't require the code to foresee and handle a multitude of exceptional circumstances.  

Rather than micromanaging my creatures movement, I tried to think of a behaviour which would by its definition prevent the above problems from occurring.

This was an interesting lesson in the challenges of artificial intelligence: there is often no simple function which can satisfy all circumstances, but hardcoding a response to each of each is also unachievable.  

I made a difficult decision.  My code which specified responses to exceptional situations was becoming bloated and impossible to maintain.  Even with a simple state machine and separate methods for each check, there were so many possible paths of execution that making any kind of high-level change would frequently break the behaviour.  This is discussed further under 'Testing'.

I came across one criteria for picking a leader which would avid the problem of collision: pick the leader closest to the goal. 

I found paths using A* form the goal point to each creature in my faction, then set the creature with the closest path to be the leader.  All other creatures in the faction were told to find a path to the leader's location.

After attempting to improve upon this with more states and getting stuck once again for the reasons explained above, I discovered something else: you can find the shortest path from one point to a group of points by performing the A* search in reverse, and adding every goal to the open list. 

Even better, you can find the shortest path given multiple goals as well, since you just exit the loop when any of the goal points are added to the closed list.  

This is the final behaviour of my faction.  It finds the shortest path between any possible destination and any possible faction creature.  That destination is set as the goal, and the creatures move towards it independently.

I realise that this behaviour isn't particularly complex.  This is just where I am now, after 117 commits and thousands of lines added/removed. If I was to repeat the assessment again, I would more ruthlessly pursue the assessment requirements, and not spend as much time on pathfinding.

My next steps would be to implement influence maps.  I already have variable tile cost, with FlamePit tiles giving a large GScore than Floor tiles.  The missing step is to do the same for enemy creatures, and propagate the values throughout the array.

This is in accordance with the mantra of creating complex behaviour by having simple AI in a complex environment.  I would rather keep the decision making for goals simple, and change behaviour by analysing and creating complexity in the game world.

--- 

## Behaviour: fulfilment of the strategy.

I have two behaviours: faction behaviour and creature behaviour.  The creature behaviour is responsible for navigating to a point.  The destination of the creature is controlled by the faction behaviour.

The faction behaviour is responsible for choosing a faction-level goal.  This is the closest reachable treasure or goal to any faction creature.

## Implementation

In order to increase class cohesion, I removed the Game and Creature parameters from most of my methods and set them as class variables.  In the event that they were not set, I throw away that game tick.

Objects which can be reused by different behaviour objects are sent as parameters in the first game tick.  The faction behaviour creates a pathfinding object and a grid, and this is reused throughout the game loop.

The result of this is to make it easier to reduce the amount of work done on each tick.  Since the grid is only initialised once for every game round, we can choose to update it infrequently and only in one place.  This would be especially useful for influence mapping.

My A* algorithm uses a Grid and Square class to search the map.  I decided early on that I wanted the tileMap to have more behaviour than the `boolean[][]` would allow.

Having a separate Grid and Square class allowed me to write useful utility methods, such as `adjacentTreasure(Square sq);` and `squareAt(Point2D p);`.  Overriding `equals()` and `hashCode()` in Square made many operations much simpler.  

An example of how this is tied together is as follows.  Whilst navigating to a destination, if a creature moves past a Square which contains treasure, the creature picks it up then returns to the square:

```
for (Square square : fGrid.getAdjacentSquares(currentSquare))
  if (square.containsTreasure())
    Treasure treasure = fGrid.getTreasureIn(square, fGame);
    if (CollisionDetection.canOccupy(fGame, fCreature, treasure.getLocation()))
      fPath.push(treasure.getLocation());

```

For A*, I maintain the OpenList using a Priority Queue.  Switching from a LinkedList which was resorted each iteration to a Priority queue roughly halved the time it takes to find paths.

This is because PriorityQueue in Java is backed by a heap.  Heaps have larger insertion and removal costs than linked lists - O(log n) vs O(1), but getting the smallest element can be done in amortised constant time - O(n log n) vs O(1).  

On every tick I compute n+1 paths, where n is the number of creatures.  One path is calculated to determine the goal, then each creature navigates to that goal.

---

## Testing

I ensured the correctness of my code by making small changes, and seeing how they affected the game state.

There was a definite positive correlation between the number of lines changed before executing the game and the amount of time spent debugging any issues.

Rather than watching the game myself, the changes I was looking for could have been automated with unit tests.

Automated testing is the only way complex behaviour can be reliably created.  Simple tactics for humans quickly translate into dozens of possible conditions and exceptions which have to be tested.

Despite not having automated tests, I still stuck to good testing disciplines.  Rather than implementing an idea for a strategy all at once, I'd incrementally change the behaviour to be more like what I want, and observe the effects this had when running the game.

I found the easiest way to test faction behaviour was to have a hero and a faction of four creatures.  Control of the hero allowed me to try and force the faction into exception circumstances, and large numbers of creatures in the faction helped reveal bugs early.

The map i created had several long corridors, multiple ways of getting to the same point, traps and fire pits.  I was attempting to recreate the worst possible map we might see in the tournament.

The only thing I didn't test in this way was the initial A* implementation.  I wasn't sure how to implement it incrementally, and so wrote it direct from pseudocode.  

In hindsight, implementing Breadth First Search and Dijkstra's algorithm separately would have allowed me to build on complexity, and would have reduced the time taken to get a working A* pathfinder.

