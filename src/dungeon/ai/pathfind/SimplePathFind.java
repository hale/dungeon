package dungeon.ai.pathfind;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

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

  public Point2D nextPoint(Point2D currentLocation, Point2D goal_pt, Game game)
  {
    List<Point2D> path = findPath(game, goal_pt);
    if (path.isEmpty()) return null;
    return path.get(0);
  }

  @Override
  protected List<Point2D> findPath(Game game, Point2D goal)
  {
    List<Point2D> pathList = new ArrayList<Point2D>();
    pathList.add(goal);
    return pathList;
  }

  public void print(Game game)
  {
    recalcMap(game);
    printMap(game);
  }



}
