package dungeon.ai;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import dungeon.App;
import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.structure.Tile;

import dungeon.model.items.treasure.Treasure;
import dungeon.model.items.Item;
import dungeon.model.items.mobs.Hero;
import dungeon.utils.State;
import dungeon.utils.Trigger;


/**
 * Class providing default behaviour for Creature mobs
 */
public class AttackBehaviour implements Behaviour
{
  /**
   * Constructor
   *
   * @param creature The creature
   */
  public AttackBehaviour(Creature creature)
  {
    fCreature = creature;
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
    if (fCreature.hasGoal())
      return move(game);

    updateState(fCreature, game);

    Point2D goal = getGoal(fCreature, game);
    if (goal !=null && CollisionDetection.canOccupy(game, fCreature, goal))
      fCreature.setGoal(goal, game);

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

  private void updateState(Creature fCreature, Game game)
  {
    if (fCreature.isInState(State.Safe))
      if (inSameRoom(game.getHero(), fCreature, game))
        fCreature.fire(Trigger.EnemyEntersRoom);

    if (fCreature.isInState(State.Threatened))
      if (!inSameRoom(game.getHero(), fCreature, game))
        fCreature.fire(Trigger.EnemyLeavesRoom);
  }

  private Point2D getGoal(Creature fCreature, Game game)
  {
    Rectangle2D bounds = getBounds(fCreature, game);
    Point2D goal_pt = null;

    switch (fCreature.getState())
    {
      case Safe:
        break;
      case Offensive: goal_pt = midpoint(fCreature, game.getHero(), bounds, game);
        break;
      case Defensive: goal_pt = safePlace(bounds);
        break;
      default: goal_pt = randomLocation(bounds, game);
    }

    return goal_pt;
  }

  boolean move(Game game)
  {
    boolean moved = false;
    if (fCreature.hasGoal())
      moved = fCreature.moveToGoal(game);
    return moved;
  }

  /* GOAL DETERMINATION */

  private Point2D randomLocation(Rectangle2D bounds, Game game)
  {
    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  private Point2D midpoint(Item item_1, Item item_2, Rectangle2D bounds, Game game)
  {
    Point2D firstLocation = item_1.getLocation();
    Point2D secondLocation = item_2.getLocation();

    double midpointX = (firstLocation.getX() + secondLocation.getX()) / 2.0;
    double midpointY = (firstLocation.getY() + secondLocation.getY()) / 2.0;

    if (bounds.contains(midpointX, midpointY))
      return new Point2D.Double(midpointX, midpointY);
    return null;
  }

  private Point2D safePlace(Rectangle2D bounds)
  {
    return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
  }

  /* UTILITY */

  private boolean takeRandomStep(Creature fCreature, Game game)
  {
    double theta = fRandom.nextDouble() * Math.PI * 2;
    return fCreature.move(theta, game);
  }

  private Rectangle2D getBounds(Creature fCreature, Game game) {
    if (AttackBehaviour.KEEP_TO_ROOMS)
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

  private boolean intEqual(Point2D p1, Point2D p2)
  {
    if (p1 == null || p2 == null) return false;
    if ((int) p1.getX() == (int) p2.getX())
      if ((int) p1.getY() == (int) p2.getY())
        return true;
    return false;
  }


}
