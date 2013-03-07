package dungeon.ai.hale;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.ArrayDeque;

import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.App;
import dungeon.ui.MapPanel;

import dungeon.ai.hale.pathfind.*;
import dungeon.ai.Behaviour;
import dungeon.ai.CollisionDetection;


public class CreatureBehaviour implements Behaviour
{
  static final boolean KEEP_TO_ROOMS = true;
  Creature fCreature;
  Game fGame;
  AStar fPathFind;
  protected void setPathFind(AStar pathFind) { this.fPathFind = pathFind; }

  Grid fGrid;
  protected void setGrid(Grid grid) { this.fGrid = grid; }


  ArrayDeque<Point2D> fPath;
  protected void setPath(ArrayDeque<Point2D> path) { fPath = path; }
  protected int getPathSize() { return fPath.size(); }
  Point2D fDest = null;
  public void setDest(Point2D dest) { this.fDest = dest; }


  public CreatureBehaviour(Creature creature)
  {
    fCreature = creature;
  }

  /* TICKS */

  @Override
    public boolean onTick(Game game)
    {
      if (fGame == null) fGame = game;
      if (fGrid == null) { App.log("fGrid null"); return false; }
      if (fPathFind == null) { App.log("fPathFind null"); return false; }

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

  /* ACTIONS */

  private boolean tryActions()
  {
    if (ActionAttack.performAction(fCreature, fGame))
      return true;
    if (ActionPickUp.performAction(fCreature, fGame))
      return true;
    if (ActionDoor.performAction(fCreature, fGame))
      return true;
    return false;
  }

  /* MOVEMENT */

  private boolean tryMovement()
  {
    if (fCreature.getGoal() != null && CollisionDetection.canOccupy(fGame, fCreature, fCreature.getGoal()))
      return fCreature.moveToGoal(fGame);
    fCreature.setGoal(null, fGame);
    return false;
  }


  private void setNewGoal()
  {
    updatePath();
    if (fPath.size() > 0)
      fCreature.setGoal(fPath.pollFirst(), fGame);
  }

  /* PATH FINDING */

  private void updatePath()
  {
    fPath = fPathFind.findPath(fCreature.getLocation(), fDest);

    // add treasure to path if treasure in adjacent square.
    Square currentSquare = new Square(fCreature.getLocation());
    for (Square square : fGrid.getAdjacentSquares(currentSquare))
      if (square.containsTreasure())
        try { // sometimes when the game ends, this causes a null pointer exception
        fPath.push(fGrid.getTreasureIn(square, fGame).getLocation());
        } catch(Exception e) { }

    //MapPanel.setPath(fPath);
    //fGrid.printSquares(fPath, fCreature.getLocation(), fDest, fGame);
  }
}
