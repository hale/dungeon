package dungeon.ai.hale;

import java.util.*;

import dungeon.model.items.mobs.Creature;
import dungeon.ai.hale.pathfind.*;

public class QLearningHelper
{
  private CreatureBehaviour fBehaviour;

  QValueStore fQTable;
  protected void setQTable(QValueStore qTable) { this.fQTable = qTable; }
  protected QValueStore getQTable() { return fQTable; }

  Action fAction = Action.MOVE_TO_GOAL;
  private boolean fTrain = false;

  public QLearningHelper(CreatureBehaviour behaviour)
  {
    this.fBehaviour = behaviour;
  }

  protected void gameOver()
  {
    if (fTrain) fQTable.saveToDisk();
  }


  protected void creatureDeath()
  {
  }

  protected void onTick(State previousState, State state)
  {
    if (!previousState.equals(state))
      updateQTable(previousState, state);
    setAction( state );
  }

  protected Action getAction() { return fAction; }

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
