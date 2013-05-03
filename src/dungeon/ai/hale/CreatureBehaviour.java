package dungeon.ai.hale;

import dungeon.App;
import dungeon.ai.hale.State;
import dungeon.ai.Behaviour;
import dungeon.ai.CollisionDetection;
import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.ai.hale.pathfind.AStar;
import dungeon.ai.hale.pathfind.Grid;
import dungeon.ai.hale.pathfind.Square;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.items.treasure.Treasure;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Controls the steps taken by a creature to reach the faction destination.
 */
public class CreatureBehaviour implements Behaviour
{
  Game fGame;
  Creature fCreature;
  ArrayDeque<Point2D> fPath;

  /* Q-learning */
  State fState = new State();
  State fPreviousState = new State();
  Action fAction = Action.MOVE_TO_GOAL;
  boolean fDead = false;
  boolean fWon;
  boolean fGameOver = false;

  QValueStore fQTable;
  protected void setQTable(QValueStore qTable) { this.fQTable = qTable; }

  AStar fPathFind;
  protected void setPathFind(AStar pathFind) { this.fPathFind = pathFind; }

  Grid fGrid;
  protected void setGrid(Grid grid) { this.fGrid = grid; }

  Point2D fDest = null;
  public void setDest(Point2D dest) { this.fDest = dest; }


  public CreatureBehaviour(Creature creature)
  {
    fCreature = creature;
  }

  /* TICKS */

  @Override
    /**
     * Is called every time in the game loop.
     *
     * If this tick happens after the faction behaviour, the first tick will be wasted.
     *
     * The behaviour specifies the following actions each time:
     *
     * 1. If possible take an action.  If not, move.
     * 2. If movement was not possible, set a new goal.
     * 3. Try to move again.
     */
    public boolean onTick(Game game)
    {
      if (fGame == null) fGame = game;
      if (fGrid == null)  return false;
      if (fPathFind == null) return false;
      if (fQTable == null) return false;

      updateState();
      if (!fPreviousState.equals(fState))
      {
        updateQTable();
      }

      boolean acted = tryActions();
      if (acted) return true;

      boolean moved = tryMovement();
      if (moved) return true;

      if (fDest == null) { return false; }

      setAction();

      setNewGoal();
      return tryMovement();
    }

  @Override
    public boolean deathTick(Game game) {
      fDead = true;
      return false;
    }

  @Override
    public boolean gameOverTick(Game game) {
      fGameOver = true;
      fWon = (fCreature.getCurrentHealth() > 0) ? true : false;
      fQTable.saveToDisk();
      return false;
    }

  private double calculateReward(State before, State after)
  {
    double reward = 0.0;
    /* POSITIVE */
    if (after.getEnergy() > before.getEnergy())
      reward = 0.5;
    if (before.isThreatened() && !after.isThreatened())
      reward = 0.5;

    /* NEGATIVE */
    if (before.getHealth() > after.getHealth())
      reward = -0.5;
    if (after.getEnergy() < 2 && after.isThreatened())
      reward = -0.5;

    //if (after.getEnergy() < 5 && after.isThreatened())
      //reward += -0.5;

    //if (after.getHealth() == 0)
      //reward = -0.5;

    //if (fGameOver)
      //reward = fWon ? 0.5 : -0.5;
    //return reward;
    //if (fPreviousState.isThreatened() == false && fState.isThreatened() == true)
      //reward = -0.5;
    return reward;
  }
  private void updateQTable()
  {
    double reward = calculateReward(fPreviousState, fState);
    double learningRate = 0.2;
    double discountRate = 0.45;
    double currentQ = fQTable.getQValue(fPreviousState, fAction);
    double maxQ = fQTable.getQValue(fState, fQTable.getBestAction(fState));

    double qValue = (1 - learningRate) * (currentQ + learningRate) * (reward +
        discountRate + maxQ);

    fQTable.storeQValue(fState, fAction, qValue);
  }

  /**
   * Update state for q learning.
   *
   * @return true if the state variables change, false otherwise.
   */
  private void updateState()
  {
    fPreviousState = fState;
    fState = new State(
        discreteEnergy(), discreteHealth(), isNearEnemies()
    );
  }

  /**
   * If any of the 8 neighboring squares contains a creature from another
   * faction, the creature's state is set to threatened.  Otherwise the state
   * is safe.
   */
  private boolean isNearEnemies()
  {
    List<Square> adjSquares = fGrid.getAdjacentSquares(
        fGrid.squareAt( fCreature.getLocation() ));
    for (Creature creat : fGame.getCreatures() )
      if (!fCreature.getFaction().equals(creat.getFaction()))
        for (Square sq : adjSquares)
          if (sq.equals(fGrid.squareAt(creat.getLocation())))
            return true;
    return false;
  }

  /**
   * @return A value between 1 and 5 representing the creature's current health.
   */
  private int discreteHealth()
  {
    return (int) Math.ceil(fCreature.getCurrentHealth() /
        fCreature.getMaxHealth() * 5);
  }

  /**
   * @return A value between 1 and 5 representing the creature's current health.
   */
  private int discreteEnergy()
  {
    return (int) Math.ceil(fCreature.getCurrentEnergy() /
        fCreature.getMaxEnergy() * 5);
  }

  /**
   * Attempts all basic action moves.
   */
  private boolean tryActions()
  {
    if      (ActionAttack.performAction(fCreature, fGame)) return true;
    else if (ActionPickUp.performAction(fCreature, fGame)) return true;
    else if (ActionDoor.performAction(fCreature, fGame))   return true;
    return false;
  }

  /**
   * Attempts to move to a predefined goal point.
   *
   * @return true if there is a goal and the collision check passes.
   *         false and un-sets the goal otherwise.
   */
  private boolean tryMovement()
  {
    if (fCreature.getGoal() != null && CollisionDetection.canOccupy(fGame, fCreature, fCreature.getGoal()))
      return fCreature.moveToGoal(fGame);
    fCreature.setGoal(null, fGame);
    return false;
  }

  private void setAction()
  {
    // do random action 20% of the time
    if ( new Random().nextInt(5) == 0)
      fAction = Action.random();
    else
      fAction = fQTable.getBestAction(fState);
    if (fAction == null)
      fAction = Action.random();
  }

  /**
   * Recalculates the path and take the next step
   */
  private void setNewGoal()
  {
    if (fAction.equals(Action.WAIT)) { return; }

    updatePath();
    addTreasureDiversionIfClose();
    if (fPath.size() > 0)
      fCreature.setGoal(fPath.pollFirst(), fGame);
  }

  /**
   * Uses A* to calculate the path from here to the goal
   */
  private void updatePath()
  {
    fPath = fPathFind.findPath(fCreature.getLocation(), fDest);
  }

  /**
   * If there is treasure in a free adjacent square, and that treasure is
   * accessible, make a diversion to pick up the treasure.
   */
  private void addTreasureDiversionIfClose()
  {
    Square currentSquare = new Square(fCreature.getLocation());
    for (Square square : fGrid.getAdjacentSquares(currentSquare))
      if (square.containsTreasure())
        try { // sometimes when the game ends, this causes a null pointer exception
          Treasure treasure = fGrid.getTreasureIn(square, fGame);
          if(CollisionDetection.canOccupy(fGame, fCreature, treasure.getLocation()))
            fPath.push(treasure.getLocation());
        } catch(Exception ignored) { }

  }
}
