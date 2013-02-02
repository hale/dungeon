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

   /* (non-Javadoc)
   * @see dungeon.ai.Behaviour#onTick(dungeon.model.Game)
   */
  public boolean onTick(Game game)
  {
    boolean hasActed = tryActions(fCreature, game);

    if (!hasActed)
      return move(game);

    return false;
  }

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

  /**
   * Make the creature move, dictated by its goal
   * <BR>
   * <UL>
   * <LI>If the creature has no goal, it will set one</LI>
   * <LI>The creature will then attempt to walk towards its goal</LI>
   * <LI>If it cannot, it will attempt to take a step in a random direction</LI>
   * </UL>
   * @param game The current game state
   * @return Returns true if the creature moved; false otherwise
   */
  boolean move(Game game)
  {
    updateGoal(fCreature, game);

    boolean moved = false;

    if (fCreature.getGoal() != null)
      moved = fCreature.moveToGoal(game);

    if (!moved)
      moved = tryRandomMovement(fCreature, game);

    return moved;
  }

  private void updateGoal(Creature fCreature, Game game)
  {
    if (fCreature.getGoal() == null)
    {
      Rectangle2D bounds = getBounds(fCreature, game); // where to look for new goal

      Point2D goal_pt = firstTreasureLocation(game);

      if (CollisionDetection.canOccupy(game, fCreature, goal_pt))
        fCreature.setGoal(goal_pt, game);
    }
  }

  private Point2D randomLocation(Rectangle2D bounds, Game game)
  {
    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  private Point2D firstTreasureLocation(Game game)
  {
    return game.getTreasure().get(0).getLocation();
  }

  private Rectangle2D getBounds(Creature fCreature, Game game) {
    if (NewBehaviour.KEEP_TO_ROOMS)
      return game.getMap().getTileAt(fCreature.getLocation()).getArea();
    else
      return game.getMap().getBounds(0);
  }

  private boolean tryRandomMovement(Creature fCreature, Game game)
  {
    double theta = fRandom.nextDouble() * Math.PI * 2;
    return fCreature.move(theta, game);
  }


  public boolean deathTick(Game game) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean gameOverTick(Game game) {
    // TODO Auto-generated method stub
    return false;
  }
}
