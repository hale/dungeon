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
import dungeon.model.items.mobs.Hero;

import dungeon.App;


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
    if (!hasActed)
      return move(game);
    return false;
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
    updateGoal(fCreature, game);

    boolean moved = false;
    if (fCreature.hasGoal())
      moved = fCreature.moveToGoal(game);
    if (!moved)
      moved = takeRandomStep(fCreature, game);
    return moved;
  }

  private void updateGoal(Creature fCreature, Game game)
  {
    if (fCreature.hasGoal())
      return;

    Rectangle2D bounds = getBounds(fCreature, game);
    Point2D goal_pt = null;

    // TODO: what about 'next point in direction of hero' instead?
    if (goal_pt == null)
      goal_pt = heroLocation(fCreature, bounds, game);

    if (goal_pt == null)
      goal_pt = randomLocation(bounds, game);

    // TODO: write a public static Point2D CollisionDetection.nearestOccupiablePoint();
    if (CollisionDetection.canOccupy(game, fCreature, goal_pt))
      fCreature.setGoal(goal_pt, game);

    return;
  }

  /* GOAL DETERMINATION */

  private Point2D randomLocation(Rectangle2D bounds, Game game)
  {
    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  private Point2D heroLocation(Creature fCreature, Rectangle2D bounds, Game game)
  {
    Hero hero = game.getHero();
    if (hero != null)
      if (withinBounds(hero, bounds))
        return hero.getLocation();
    return null;
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


}
