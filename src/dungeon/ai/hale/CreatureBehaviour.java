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
import java.util.ArrayDeque;
import java.util.List;

/**
 * Controls the steps taken by a creature to reach the faction destination.
 */
public class CreatureBehaviour implements Behaviour
{
  Game fGame;
  Creature fCreature;
  ArrayDeque<Point2D> fPath;

  /* States for Q-learning */
  State fState = new State();

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

      if (updateState())
      {
        System.out.print(fCreature.getFaction() + " ");
        System.out.print(fCreature + ": ");
        System.out.println( fState );
      }

      boolean acted = tryActions();
      if (acted) return true;

      boolean moved = tryMovement();
      if (moved) return true;

      if (fDest == null) { return false; }

      setNewGoal();
      return tryMovement();
    }

  @Override
    public boolean deathTick(Game game) {
      return false;
    }

  @Override
    public boolean gameOverTick(Game game) {
      return false;
    }

  /**
   * Update state for q learning.
   *
   * @return true if the state variables change, false otherwise.
   */
  private boolean updateState()
  {
    State newState = new State(
        discreteEnergy(), discreteHealth(), isNearEnemies()
    );
    if( newState.equals(fState) )
      return false;
    fState = newState;
    return true;
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

  /**
   * Recalculates the path and take the next step
   */
  private void setNewGoal()
  {
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
