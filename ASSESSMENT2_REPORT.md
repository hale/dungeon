Philip Hale - 50907446 - p.hale.09@aberdeen.ac.uk

# CS3523 Assessment 1: AI Tournament
Set: March 11, 2013
Due: May 3, 2013

---

## File list

```
src/dungeon/ai/hale
|── Action.java
├── CreatureBehaviour.java
├── FactionBehaviour.java
├── State.java
├── pathfind
│   ├── AStar.java
│   ├── Grid.java
│   └── Square.java
└── qlearning
    ├── ActionState.java
    ├── QLearningHelper.java
    ├── QValueStore.java
    └── QValueStore.ser

2 directories, 11 files
```

## Usage

Extract the included source archive into `/src/dungeon/ai`.  My package assumes
that the other classes in `dungeon.ai` have not been modified.

The Faction Behaviour attribute in the map file should be set like so:

    [...]
    <Factions>
      <Vector>
        <Faction Behaviour="dungeon.ai.hale.FactionBehaviour" [...]/>
        [...]
      </Vector>
    </Factions>
    [...]

By default the behaviour will run using data precomputed in
`src/dungeon/ai/hale/qlearning/QValueStore.ser`. In order to learn more, change
the fTrain class variable in QLearningHelper from false to true.

In order to reset the QLearning data, blank the file with
`: > src/dungeon/ai/hale/qlearning/QValueStore.ser`.

## Description of the learning algorithm

I measured the following state variables:

* Creature energy level (integer 1 to 5)
* Creature health level (integer 1 to 5)
* Whether the creature is threatened or not. (true or false)
* Distance in squares from creature to goal (1 to the number of squares on the
  board - typically values no larger than 1/4 this maximum)

And had the following actions:

* Move towards goal.
* Stand still.

The reward value is between -1.0 and +1.0, and is calculated based on the
difference between the state of two game ticks. The rewards are cumulative.

| Description                    | Reward modifier |
| :-                             | :-:             |
| No longer threatened           | +0.5            |
| Closer to goal                 | +0.1            |
| In same place                  | -0.1            |
| Newly threatened               | -0.2            |
| Creature dead                  | -0.4            |
| Newly threatened and no energy | -0.3            |

'No longer threatened' implies an enemy mob has run away or been killed by the
faction, and therefore represents the largest positive reward.

If the creature becomes newly threatened, a slight negative reward is given.
This is based on the assumption that factions which avoid danger are likely to
win more games.

A slightly larger negative reward is given if in addition to being newly
threatened, the creature has no energy.  This is as a result of observing the
creatures behaviour, and noticing that the ogre often dies when attacking
without sufficient energy.

In addition, a small reward is given for reducing the distance between the
creature and the goal.  Since I am not tracking which faction wins the game or
the quantity of treasure, this is required to prevent waiting being overly
rewarded.

## Training process

Initially, the algorithm would take random actions 20% of the time, and the
rest of the time take the best action as determined by the Q Table. However,
this cornered the learning into paritcular (bad) behaviours - such as not
moving at all, or never waiting.  In addition, the learning would take to long
when only acting randomly 20% of the time.  A class variable in
`QLearningHelper.java` toggles either saving learning data into the Q-Table and
acting randomly, or only reading the Q-Table and taking the best learnt action.

The Q-Table is trained on mapQ1.xml against Collinson behaiour, and itself on
pathfind3.xml  The included learning data is the result of approximately 100
tournaments. ~90 state-action pairs have been encountered during learning.

The QTable HashMap is serialised and saved in a flat file, and updated at the
end of each game.

Generating significantly more training data was not possible due to the
limitations of the game engine. Sometimes the creatures would get stuck,
requiring manual intervention to continue the training process. Further and
better training would be possible if the dungeon layout and item placement
could be programatically randomised.

## Analysis of behaviour

Q-learning was employed in this implementation to overcome limitations of the
previous behaviour.  The faction could optimally pathfind to treasure and
enemies, but did not react to changes in health or energy.  The result was
blind pursuit of the enemy factions, often resulting in lost games.  

From observing the behaviour of my faction, I decided to reward and 'punish' the
creatures for attacking the enemy with low energy.  By limiting the scope of
learning to a known goal, the challenge of tuning the algorithm was made
simpler than simply rewarded overall game wins.

The greatest challenge in programming this dungeon has been balancing complex
behaviour with reliable and understandable code.  Conceptual AI behaviour, when
implemented as you might express it linguistically, quickly spirals into dozens
of edge cases and large quantities of code. Reinforcement learning techniques
allow for the autonomous selection of actions in a large number of different
game states. The different situations which can occur in even a simple game are
played out, and given predefined rewards the action taken can be measured and
recorded.

## Limitations

Despite the behaviour improving perceptually, the overall number of games won
has not increased significantly. In addition, because of the large number of
possible distance (pathLength) values, some suboptimal actions can be observed
despite reasonably large amounts of training.

## Conclusion

I began with the hypothesis that merely rewarding game wins and losses would
not result in better behaviour.  Instead, QLearning should be employed to
direct the creature's actions towards certain behavioural goals.  In the case
of my behaviour, this was to wait when the energy level was below a certain
thresh-hold. I hoped that using Q-Learning to achieve this goal would result in
more nuanced and reliable behaviour than if that had been implemented
procedurally.

* no simple and quick way to train the game (sometimes get stuck, can't
  randomise dungoen layout, can't randomise dungeons)
