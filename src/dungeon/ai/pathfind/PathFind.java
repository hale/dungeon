package dungeon.ai.pathfind;

import java.awt.geom.Point2D;
import java.util.List;

import dungeon.App;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;

/**
 * Instances of this class are used for pathfinding
 * (once it has been completed).
 */
public abstract class PathFind {

  Creature fCreature = null;

  public PathFind(Creature creature, Game game) {
    fCreature = creature;
  }

  abstract protected List<Point2D> findPath(Point2D origin, Point2D goal);


}
