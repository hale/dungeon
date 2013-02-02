package dungeon.ai;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.structure.Tile;

import dungeon.collections.TreasureList;
import dungeon.model.items.treasure.Treasure;
import dungeon.model.items.Item;


/**
 * Class providing default behaviour for Creature mobs
 */
public class NewBehaviour implements Behaviour
{
  /**
   * Constructor
   *
   * @param creature The creature
   */
  public NewBehaviour(Creature creature)
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
    Point2D goal_pt = achievableTreasureLocation(bounds, game);

    if (CollisionDetection.canOccupy(game, fCreature, goal_pt))
      fCreature.setGoal(goal_pt, game);
  }


  /* GOAL DETERMINATION */

  private Point2D randomLocation(Rectangle2D bounds, Game game)
  {
    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  private Point2D treasureLocation(Rectangle2D bounds, Game game)
  {
    for (Treasure treasure : game.getTreasure())
    {
      if (withinBounds(treasure, bounds))
        return treasure.getLocation();
    }
    return randomLocation(bounds, game);
  }

  private Point2D achievableTreasureLocation(Rectangle2D bounds, Game game)
  {
    for (Treasure treasure : game.getTreasure()) {
      if (inSameRoom(fCreature, treasure, game))
        if (canCarryTreasure(fCreature, treasure, game))
          return treasure.getLocation();
    }
    return randomLocation(bounds, game);
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

    if (item_one_tile.getID() == item_two_tile.getID())
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

}
