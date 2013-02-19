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

    PriorityQueue<Square> openList = new PriorityQueue<Square>(400,
        new Comparator<Square>()
        {
          public int compare(Square a, Square b)
          {
            return a.getFCost() - b.getFCost();
          }
        }
      );

    // 1. add the starting square to the open list
    openList.add(originSquare);

    // 2. Repeat the following until the open list is empty. (this means there is no path).
    // FIXME: switch foreach to iterator (which allows modification)
    for (Square square : openList)
    {
      // (i) for each adjacent square



      // if goalSquare is in open list, exit the loop because path has been found.
    }

    // if path has been found, calculate the path
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
