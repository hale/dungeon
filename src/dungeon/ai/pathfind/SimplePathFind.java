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
    ArrayList<Square> closedList = new ArrayList<Square>();

    // 1. add the starting square to the open list
    openList.add(originSquare);

    boolean pathFound = false;
    int gScore;
    int hScore;
    // 2. Repeat the following until the open list is empty.
    while (!openList.isEmpty())
    {
      // a) Set current square as lowest-fScore square from the open list
      Square currentSquare = openList.poll();
      // b) Move current square to the closed list
      closedList.add(currentSquare);
      // (i)   If goalSquare is in closed list, path has been found.
      if (closedList.contains(goalSquare)) {
        pathFound = true;
        break;
      }
      // c) For each adjacent square to current square...
      for (Square adjSquare : grid.getAdjacentSquares(currentSquare))
      {
        // (i)   Ignore if unreachable or in closed list
        if (adjSquare.isUnreachable() || closedList.contains(adjSquare))
          continue;
        // (ii)  If it isn't on the open list,
        if (!openList.contains(adjSquare))
        {
          // * Add it to the open list.
          openList.offer(adjSquare);
          // * Calculate F, G, H score.
          gScore = adjSquare.getMoveCost() + adjSquare.getParent().getGScore();
          adjSquare.setGScore(gScore);
          adjSquare.setHScore(grid.chebyshev(currentSquare, goalSquare));
          // * Set its parent square to the current square.
          adjSquare.setParent(currentSquare);
        // (iii) If it is already in the open list:
        } else
        {
          //  * Check to see if this path to that square is better, using G cost as the measure.
          //    A lower G cost means that this is a better path. If so, change the parent of the
          //    square to the current square, and recalculate the G and F scores of the square.
          //    If you are keeping your open list sorted by F score, you may need to resort the
          //    list to account for the change.
          if (currentSquare.getGScore() + adjSquare.getMoveCost() < adj.getGScore())
          {
            adjSquare.setParent(currentSquare());
            gScore = adjSquare.getMoveCost() + adjSquare.getParent().getGScore();
            adjSquare.setGScore(gScore);
            adjSquare.setHScore(grid.chebyshev(currentSquare, goalSquare));
          }
        }
      }
    }
    // TODO:....
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
