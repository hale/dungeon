Philip Hale - 50907446 - p.hale.09@aberdeen.ac.uk

# CS3523 Assessment 1: AI Tournament

**Set:** March 11, 2013 **Due:** May 3, 2013

---

## File list

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
the `fTrain` class variable in `QLearningHelper` from false to true.

In order to reset the `QLearning` data, blank the file with
`: > src/dungeon/ai/hale/qlearning/QValueStore.ser`.

## Description of the learning algorithm

* **Learning rate:** 0.2
* **Discount rate:** 0.35

A low learning rate is chosen because of the large number of training
runs required for improved behaviour.  This in turn is necessary because the set of
action-state pairs is quite large.

The discount rate is the result of trial and error, where much higher values
would quickly cause Q-values to reach +/-Infinity.

The algorithm uses a HashMap for the data store.  I wanted to avoid primitive
types (such as a 2D array), since using object orientation would allow the data
structure to be more easily changed at a later point.

Since the Q-Table associates action-state pairs with the Q-values, I
created an `ActionState` wrapper class to identify a unique action and state
combination.  Overriding `equals()` and `hashCode()` simplified Q-table updates.

The Q-Learning implementation is based on pseudocode taken from Chapter 7 of
*Artificial Intelligence for Games* (2009) by Ian Millington and John Funge.

### State variables

* Creature energy level (integer 1 to 5).
* Creature health level (integer 1 to 5).
* Whether the creature is threatened or not. (true or false).
* Distance in squares from creature to goal (1 to the number of squares on the
  board - typically values no larger than 1/4 this maximum).

I had initially hoped to achieve success from just measuring the first three of
the above, but this resulted in an overvaluation of the `WAIT` action, where
the creature would do nothing.  Tracking distance to goal allowed a reward to
be placed on movement that takes the creature closer to its destination.
Using the path computed with A\* ensures a more accurate measure of distance
than a simple Euclidian calculation, and has the added benefit of already being
a small(ish) discrete value.

In order to determine whether the creature is under threat or not, the eight
adjacent squares are inspected for the presence of enemy mobs.  This was
relatively simple to implement, since I could reuse methods from the
pathfinding exercise.

### Actions

* Move towards goal.
* Stand still.

Given the wide range of states and rewards, I decided to limit the available
actions to moving or not moving. Adding additional complex
behaviour was beyond the scope of this exercise.

### Rewards

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
rest of the time take the best action as determined by the Q-Table. However,
this cornered the learning into particular (bad) behaviours - such as not
moving at all, or never waiting.  In addition, the learning would take too long
when only acting randomly 20% of the time.  To combat this, a class variable in
`QLearningHelper.java` toggles either saving learning data into the Q-Table and
acting randomly, or only reading the Q-Table and taking the best learnt action.

The Q-Table is trained on `mapQ1.xml` against Collinson behaiour, and  against
itself on pathfind3.xml  The included learning data is the result of
approximately 100 tournaments. ~120 state-action pairs have been encountered
during learning.

The Q-Table HashMap is serialised and saved in a flat file, and updated at the
end of each game.

Generating significantly more training data was not possible due to limitations
of running tournaments. Sometimes the creatures would get stuck, requiring
manual intervention to continue the training process. Further and better
training would be possible if the dungeon layout and item placement could be
programatically randomised.

## Analysis of behaviour

Q-Learning was employed in this implementation to overcome limitations of the
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

The result matched my expectations.  In general, the ogre will wait after the
first longbow shot until its energy is replenished.  In addition, when a
faction creature is near the enemy they do not wait, since there isn't as much
benefit. Other behavioural differences can be observed, but it's more difficult
to see a pattern.

## Limitations

Despite the behaviour improving perceptually, the overall number of games won
has not increased significantly. 

In addition, because of the large number of
possible distance (pathLength) values, some suboptimal actions can be observed
despite reasonably large amounts of training.

The other limitation resulted from the small range of available actions.
Implementing some form of evasion would have widened the possibility of
actions, and with appropriate rewards led to more complex and effective
behaviour.

## Conclusion

I began with the hypothesis that merely rewarding game wins and losses would
not result in better behaviour.  Instead, Q-Learning could be employed to
direct the creature's actions towards certain behavioural goals.  In the case
of my behaviour, when the energy level was below a certain thresh-hold the
creature should (normally) wait. Using Q-Learning to achieve this goal would
result in more nuanced and reliable behaviour than if that had been implemented
procedurally.

Sometimes, it is more important for the AI players to behave according to an
understandable logic than to always take the best possible action.  One
possible negative consequence of learning algorithms is they can uncover
exploits in the game world which might not be entertaining to human players.
This effect can be mitigated by placing limitations on the action sequences of
creatures.  It raises the interesting possibility of rewarding entertaining
behaviour and punishing what players might consider 'cheating'.




