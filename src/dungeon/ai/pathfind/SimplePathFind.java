package dungeon.ai.pathfind;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

import dungeon.App;
import dungeon.ai.*;
import dungeon.ai.pathfind.*;
import dungeon.model.*;
import dungeon.model.items.mobs.Creature;
import dungeon.utils.*;

public class SimplePathFind extends PathFind {

  PriorityQueue<Square> openList = new PriorityQueue<Square>(400,
    new Comparator<Square>()
    {
      public int compare(Square a, Square b)
      {
        return a.getFCost() - b.getFCost();
      }
    }
  );

  Grid grid;
  public SimplePathFind(Creature creature, Game game)
  {
    super(creature, game);
    this.grid = new Grid(fCreature, game);
  }

  public Point2D nextPoint(Point2D currentLocation, Point2D goal_pt)
  {
    List<Point2D> path = findPath(currentLocation, goal_pt);
    if (path.isEmpty()) return null;
    return path.get(0);
  }

  @Override
  protected List<Point2D> findPath(Point2D origin, Point2D goal)
  {
    Square originSquare = new Square(origin);
    Square goalSquare = new Square(goal);

    // 1. add the starting square to the open list
    openList.add(originSquare);

    // 2. Repeat the following until the open list is empty.
    //   a) Set current square as lowest-fScore square from the open list
    //
    //   b) Move current square to the closed list
    //     (i)   If goalSquare is in closed list, path has been found.
    //
    //   c) For each adjacent square to current square...
    //     (i)   Ignore if unreachable or in closed list
    //     (ii)  If it isn't on the open list,
    //       * Add it to the open list.
    //       * Calculate F, G, H score.
    //       * Set its parent square to the current square.
    //    (iii) If it is already in the open list:
    //       * Check to see if this path to that square is better, using G cost as the measure.
    //         A lower G cost means that this is a better path. If so, change the parent of the
    //         square to the current square, and recalculate the G and F scores of the square.
    //         If you are keeping your open list sorted by F score, you may need to resort the
    //         list to account for the change.
    //
    // 3. If path has been found, calculate the path
    //   (i)   Working backwards from the target square, go from each square to its
    //         parent square until you reach the starting square.
    List<Point2D> pathList = new ArrayList<Point2D>();
    pathList.add(goal);
    return pathList;
  }



  private Square[] neighbours(Square square)
  {
    Square[] neighbours = new Square[8];
    for (int i = 0; i < 8; i++)
    {
      //neighbours[i] = new Square(
    }
    return neighbours;
  }


}
