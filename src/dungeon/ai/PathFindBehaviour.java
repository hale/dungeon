package dungeon.ai;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.Stack;

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

import dungeon.ai.pathfind.*;


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
    SimplePathFind spf = new SimplePathFind(fCreature);
  }
  Creature fCreature = null;
  Random fRandom = new Random();

  static final boolean KEEP_TO_ROOMS = true;

  /* TICKS */

  public boolean onTick(Game game)
  {
    boolean hasActed = tryActions(fCreature, game);
    if (hasActed)
      return false;

    Point2D goal = getGoal(fCreature, game);
    Point2D step = nextStepToGoal(goal, fCreature, game);

    if (step != null)
      fCreature.setGoal(step, game);

    return move(game);
  }

  public boolean deathTick(Game game) {
    return false;
  }

  public boolean gameOverTick(Game game) {
    return false;
  }

  /* ACTIONS */

  private boolean tryActions(Creature fCreature, Game game)
  {
    if (ActionAttack.performAction(fCreature, game))
      return true;
    if (ActionPickUp.performAction(fCreature, game))
      return true;
    if (ActionDoor.performAction(fCreature, game))
      return true;
    return false;
  }

  /* MOVEMENT */

  boolean move(Game game)
  {

    boolean moved = false;
    if (fCreature.hasGoal())
      moved = fCreature.moveToGoal(game);
    return moved;
  }

  private Point2D getGoal(Creature fCreature, Game game)
  {
    if (fCreature.hasGoal())
      return fCreature.getGoal();

    Rectangle2D bounds = getBounds(fCreature, game);
    Point2D goal_pt = null;

    if (goal_pt == null)
      goal_pt = randomLocation(bounds, game);

    if (CollisionDetection.canOccupy(game, fCreature, goal_pt))
      if (!samePlace(goal_pt, fCreature.getLocation()))
        return goal_pt;

    return null;
  }

  /* GOAL DETERMINATION */

  private Point2D randomLocation(Rectangle2D bounds, Game game)
  {
    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  /* PATH FINDING */

  private Point2D nextStepToGoal(Point2D goal, Creature fCreature, Game game)
  {
    if (goal == null) return null;
    // TODO: use path finding to calculate intermediaries.
    return goal;
  }

  /* UTILITY */

  private boolean takeRandomStep(Creature fCreature, Game game)
  {
    double theta = fRandom.nextDouble() * Math.PI * 2;
    return fCreature.move(theta, game);
  }

  private Rectangle2D getBounds(Creature fCreature, Game game) {
    if (NewBehaviour.KEEP_TO_ROOMS)
      return game.getMap().getTileAt(fCreature.getLocation()).getArea();
    else
      return game.getMap().getBounds(0);
  }

  private boolean withinBounds(Item item, Rectangle2D bounds)
  {
    if (bounds.contains(item.getLocation().getX(), item.getLocation().getY()))
      return true;
    return false;
  }

  private boolean inSameRoom(Item item_1, Item item_2, Game game)
  {
    Tile item_one_tile = game.getMap().getTileAt(item_1.getLocation());
    Tile item_two_tile = game.getMap().getTileAt(item_2.getLocation());

    if (item_one_tile.equals(item_two_tile))
      return true;
    return false;
  }

  private boolean canCarryTreasure(Creature fCreature, Treasure treasure, Game game)
  {
    double t_weight = treasure.getWeight();
    double encumbrance = fCreature.getEncumbrance();
    int strength = fCreature.getStrength();

    if (encumbrance + t_weight < strength)
      return true;
    return false;
  }

  private boolean isAchievable(Treasure treasure, Creature fCreature, Rectangle2D bounds, Game game)
  {
    if (withinBounds(treasure, bounds)) {
      if (inSameRoom(fCreature, treasure, game)) {
        if (canCarryTreasure(fCreature, treasure, game)) {
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
