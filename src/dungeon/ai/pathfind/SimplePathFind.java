package dungeon.ai.pathfind;

import java.awt.geom.Point2D;
import java.util.List;

import dungeon.App;
import dungeon.ai.*;
import dungeon.ai.pathfind.*;
import dungeon.model.*;
import dungeon.model.items.mobs.Creature;
import dungeon.utils.*;

public class SimplePathFind extends PathFind {

  public SimplePathFind(Creature creature)
  {
    super(creature);
  }

  public List<Point2D> findPath(Game game, Point2D goal)
  {
    return null;
  }



}
