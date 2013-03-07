package dungeon.ai.hale;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

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


public class PatientPathFindBehaviour implements Behaviour
{
  static final boolean KEEP_TO_ROOMS = true;
  Creature fCreature;
  Game fGame;
  SimplePathFind fPathFind;
  protected void setPathFind(SimplePathFind pathFind) { this.fPathFind = pathFind; }

  Grid fGrid;
  protected void setGrid(Grid grid) { this.fGrid = grid; }


  LinkedList<Point2D> fPath;
  protected void setPath(List<Point2D> path)
  {
    fPath = (LinkedList<Point2D>) path;
  }
  protected int getPathSize() { return fPath.size(); }
  Point2D fDest = null;
  public void setDest(Point2D dest) { this.fDest = dest; }


  public PatientPathFindBehaviour(Creature creature)
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
    assert(fDest != null);
    fPath = (LinkedList<Point2D>) fPathFind.findPath(
        fCreature.getLocation(), fDest);

    Square currentSquare = new Square(fCreature.getLocation());
    for (Square square : fGrid.getAdjacentSquares(currentSquare))
      if (square.containsTreasure())
        fPath.push(fGrid.getTreasureIn(square, fGame).getLocation());

    //MapPanel.setPath(fPath);
    //fGrid.printSquares(fPath, fCreature.getLocation(), fDest, fGame);
  }
}
