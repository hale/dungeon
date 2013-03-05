package dungeon.ai.hale;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;

import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;

import dungeon.ai.hale.pathfind.*;
import dungeon.ai.Behaviour;


public class PatientPathFindBehaviour implements Behaviour
{
  static final boolean KEEP_TO_ROOMS = true;
  Creature fCreature;
  Game fGame;
  SimplePathFind fPathFind;
  LinkedList<Point2D> fPath;
  protected void setPath(List<Point2D> path)
  {
    fPath = (LinkedList<Point2D>) path;
  }
  protected int getPathSize() { return fPath.size(); }
  Point2D fGoal = null;;
  public void setGoal(Point2D goal) { this.fGoal = goal; }


  public PatientPathFindBehaviour(Creature creature)
  {
    fCreature = creature;
  }

  /* TICKS */

  @Override
    public boolean onTick(Game game)
    {
      if (fGame == null) fGame = game;
      if (fPathFind == null) fPathFind = new SimplePathFind(fCreature, fGame);

      return (tryActions() || tryMovement());
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
    if (fCreature.getGoal() != null)
      return fCreature.moveToGoal(fGame);

    if (fGoal == null)
      return false;

    updatePath();
    if (fPath.size() > 2)
    {
      fCreature.setGoal(fPath.pollFirst(), fGame);
      return fCreature.moveToGoal(fGame);
    }
      return false;
  }

  /* PATH FINDING */

  private void updatePath()
  {
    assert(fGoal != null);
    fPath = (LinkedList<Point2D>) fPathFind.findPath(
        fCreature.getLocation(), fGoal);
  }
}
