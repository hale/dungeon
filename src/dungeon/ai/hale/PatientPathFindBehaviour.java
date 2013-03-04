package dungeon.ai.hale;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.LinkedList;

import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.structure.Tile;

import dungeon.collections.TreasureList;
import dungeon.model.items.treasure.Treasure;
import dungeon.model.items.Item;

import dungeon.App;

import dungeon.ai.hale.pathfind.*;
import dungeon.ai.Behaviour;


public class PatientPathFindBehaviour implements Behaviour
{
  static final boolean KEEP_TO_ROOMS = true;
  Creature fCreature;
  Game fGame;
  SimplePathFind fPathFind;
  Point2D fGoal;

  public PatientPathFindBehaviour(Creature creature)
  {
    fCreature = creature;
  }

  public void setGoal(Point2D goal) { this.fGoal = goal; }

  /* TICKS */

  @Override
    public boolean onTick(Game game)
    {
      if (fGame == null) fGame = game;
      if (fPathFind == null) fPathFind = new SimplePathFind(fCreature, fGame);

      boolean hasActed = tryActions();
      if (hasActed)
        return false;

      return move();
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

  private boolean move()
  {
    if (fGoal == null) { return false; }
    if (fCreature.getGoal() == null) {
      Point2D nextStep = pathfindTo(fGoal);
      if (nextStep != null)
        fCreature.setGoal(nextStep, fGame);
    }
    return fCreature.moveToGoal(fGame);
  }

  /* PATH FINDING */

  private Point2D pathfindTo(Point2D destination)
  {
    LinkedList<Point2D> path = (LinkedList<Point2D>) fPathFind.findPath(
        fCreature.getLocation(), destination);

    return path.pollFirst();
  }
}
