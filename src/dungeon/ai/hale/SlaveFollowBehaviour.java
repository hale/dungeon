package dungeon.ai.hale;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;

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
import dungeon.ai.CollisionDetection;


/**
 * Class providing default behaviour for Creature mobs
 */
public class SlaveFollowBehaviour implements Behaviour
{
  /**
   * Constructor
   *
   * @param creature The creature
   */
  public SlaveFollowBehaviour(Creature creature)
  {
    fCreature = creature;
    path = new LinkedList<Point2D>();
  }
  Creature fCreature = null;
  Random fRandom = new Random();
  Game fGame;
  List<Point2D> path;
  protected void setPath(List<Point2D> path) { this.path = path; }

  static final boolean KEEP_TO_ROOMS = true;

  protected boolean hasPath()
  {
    return (this.path.isEmpty()) ? false : true;
  }

  /* TICKS */

  @Override
    public boolean onTick(Game game)
    {
      if (fGame == null) fGame = game;

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
    if (fCreature.getGoal() == null)
    {
      Point2D goal = ((LinkedList<Point2D>) path).poll();
      if (goal != null)
        fCreature.setGoal(goal, fGame);
    }
    return fCreature.moveToGoal(fGame);
  }

}
