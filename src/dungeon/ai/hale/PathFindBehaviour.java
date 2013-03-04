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


/**
 * Class providing default behaviour for Creature mobs
 */
public class PathFindBehaviour implements Behaviour
{
  /**
   * Constructor
   *
   * @param creature The creature
   */
  public PathFindBehaviour(Creature creature)
  {
    fCreature = creature;
  }
  Creature fCreature = null;
  Random fRandom = new Random();
  SimplePathFind fPathFind;
  Game fGame;

  static final boolean KEEP_TO_ROOMS = true;


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
    if (fCreature.getGoal() == null)
      setNewGoal();

    return fCreature.moveToGoal(fGame);
  }

  private void setNewGoal()
  {
    Point2D destination = getDestination();
    pathfindTo(destination);
  }

  private Point2D getDestination()
  {

    //Point2D goal_pt = randomLocation();

    Point2D goal_pt = fGame.getHero().getLocation();

    //if (CollisionDetection.canOccupy(fGame, fCreature, goal_pt))
      //if (!samePlace(goal_pt, fCreature.getLocation()))
        return goal_pt;

    //App.log("Returning null goal.");
    //return null;
  }

  /* GOAL DETERMINATION */

  private Point2D randomLocation()
  {
    Rectangle2D bounds = getBounds();
    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  /* PATH FINDING */

  private void pathfindTo(Point2D destination)
  {
    LinkedList<Point2D> path = (LinkedList<Point2D>) fPathFind.findPath(
        fCreature.getLocation(), destination);

    Point2D nextPoint;
    if (!path.isEmpty())
    {
      nextPoint = path.pop();
      fCreature.setGoal(nextPoint, fGame);
    }
  }

  /* UTILITY */

  private boolean takeRandomStep()
  {
    double theta = fRandom.nextDouble() * Math.PI * 2;
    return fCreature.move(theta, fGame);
  }

  private Rectangle2D getBounds() {
    if (this.KEEP_TO_ROOMS)
      return fGame.getMap().getTileAt(fCreature.getLocation()).getArea();
    else
      return fGame.getMap().getBounds(0);
  }

  private boolean withinBounds(Item item)
  {
    Rectangle2D bounds = getBounds();
    if (bounds.contains(item.getLocation().getX(), item.getLocation().getY()))
      return true;
    return false;
  }

  private boolean inSameRoom(Item item_1, Item item_2)
  {
    Tile item_one_tile = fGame.getMap().getTileAt(item_1.getLocation());
    Tile item_two_tile = fGame.getMap().getTileAt(item_2.getLocation());

    if (item_one_tile.equals(item_two_tile))
      return true;
    return false;
  }

  private boolean canCarryTreasure(Treasure treasure)
  {
    double t_weight = treasure.getWeight();
    double encumbrance = fCreature.getEncumbrance();
    int strength = fCreature.getStrength();

    if (encumbrance + t_weight < strength)
      return true;
    return false;
  }

  private boolean isAchievable(Treasure treasure, Rectangle2D bounds)
  {
    if (withinBounds(treasure)) {
      if (inSameRoom(fCreature, treasure)) {
        if (canCarryTreasure(treasure)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean samePlace(Point2D p1, Point2D p2)
  {
    if (p1 == null || p2 == null) return false;
    if (Math.round(p1.getX()) == Math.round(p2.getX()))
      if (Math.round(p1.getY()) == Math.round(p2.getY()))
        return true;
    return false;
  }


}
