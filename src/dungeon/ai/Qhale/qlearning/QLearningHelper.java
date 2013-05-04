package dungeon.ai.Qhale.qlearning;

import dungeon.ai.Qhale.*;

/**
 * Helper class to manage QLearning for a creature's behaviour.
 */
public class QLearningHelper
{

  QValueStore fQTable;
  public QValueStore getQTable() { return fQTable; }
  public void setQTable(QValueStore qTable) { this.fQTable = qTable; }

  Action fAction = Action.MOVE_TO_GOAL;
  public Action getAction() { return fAction; }

  /** Toggle building the table and learning from it. */
  private boolean fTrain = false;

  /**
   * Equivalent to the gameOver() tick in Behaviour.
   */
  public void gameOver()
  {
    if (fTrain) fQTable.saveToDisk();
  }

  /**
   * Called every tick in the creature's behaviour class
   *
   * @param previousState State from the previous tick.
   * @param state Current state.
   */
  public void onTick(State previousState, State state)
  {
    if (!previousState.equals(state))
      updateQTable(previousState, state);
    setAction( state );
  }


  /**
   * Picks an action based on the current state.  If fTrain is set to true, the
   * action is chosen randomly (for quicker learning). If fTrain is false, the
   * QTable is used to get the best action. A compromise between the two is
   * commented.
   *
   * @param state Current state.
   */
  private void setAction(State state)
  {
    // do random action 20% of the time
    //if ( new Random().nextInt(5) == 0)
    if (fTrain)
      fAction = Action.random();
    else
      fAction = fQTable.getBestAction(state);
    if (fAction == null)
      fAction = Action.random();
  }

  /**
   * Picks a reward value for a given state change.
   *
   * @param before State from the previous tick.
   * @param after State from the current tick.
   * @return A double value x, where x is between -1 and 1
   */
  private double calculateReward(State before, State after)
  {
    double reward = 0.0;
    /* POSITIVE */
    if (before.isThreatened() && !after.isThreatened())
      reward += 0.5;
    if (after.getPathSize() < before.getPathSize())
      reward += 0.1;

    /* NEGATIVE */
    if (after.getPathSize() == before.getPathSize())
      reward += -0.1;
    if (!before.isThreatened() && after.isThreatened())
      reward += -0.2;
    if (after.getHealth() == 0)
      reward += -0.4;
    if (!before.isThreatened() && after.isThreatened())
      if (after.getEnergy() == 0)
        reward += -0.3;

    return reward;
  }
  /**
   * Adds or updates a value to the q table.
   *
   * @param previousState State from the previous tick.
   * @param state State from this ticks.
   */
  private void updateQTable(State previousState, State state)
  {
    double reward = calculateReward(previousState, state);
    double learningRate = 0.2;
    double discountRate = 0.35;
    double currentQ = fQTable.getQValue(previousState, fAction);
    double maxQ = fQTable.getQValue(state, fQTable.getBestAction(state));

    double qValue = (1 - learningRate) * (currentQ + learningRate) * (reward +
        discountRate + maxQ);

    fQTable.storeQValue(state, fAction, qValue);
  }
}
