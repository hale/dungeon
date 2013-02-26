package dungeon.ai.pathfind;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Comparator;

import dungeon.App;
import dungeon.ai.*;
import dungeon.ai.pathfind.*;
import dungeon.model.*;
import dungeon.model.items.mobs.Creature;
import dungeon.utils.*;

public class SimplePathFind extends PathFind {

  Grid grid;
  TreeSet<Square> openList = new TreeSet<Square>(
    new Comparator<Square>() {
      public int compare(Square a, Square b) {
        return a.getFCost() - b.getFCost();
      }
    }
  );

  public SimplePathFind(Creature creature, Game game)
  {
    super(creature, game);
    this.grid = new Grid(fCreature, game);
    grid.printSqGrid();
  }

  @Override
  public List<Point2D> findPath(Point2D pointA, Point2D pointB)
  {
    Square originSquare = new Square(pointA);
    Square goalSquare = new Square(pointB);
    return squaresToPoints(findPath(originSquare, goalSquare));
  }

  private List<Point2D> squaresToPoints(List<Square> squares)
  {
    List<Point2D> points = new LinkedList<Point2D>();
    for (Square square : squares)
      points.add(square.getCenter());
    return points;
  }

  private LinkedList<Square> findPath(Square originSquare, Square goalSquare)
  {
    System.out.println("\033[3J");
    ArrayList<Square> closedList = new ArrayList<Square>();

    // 1. add the starting square to the open list
    openList.add(originSquare);

    boolean pathFound = false;
    int gScore;
    int hScore;
    // 2. Repeat the following until the open list is empty.
    while (!openList.isEmpty() && !pathFound)
    {
      System.out.println("\033[32m open list contains " + openList.size() + " squares...\033[0m");
      // a) Set current square as lowest-fScore square from the open list
      Square currentSquare = openList.pollFirst();
      // b) Move current square to the closed list
      System.out.println("\033[31m Adding square " + currentSquare + " with fCost " + currentSquare.getFCost() + " to the closed list.\033[0m");
      closedList.add(currentSquare);
      // (i)   If goalSquare is in closed list, path has been found.
      if (closedList.contains(goalSquare))
        pathFound = true;
      // c) For each adjacent square to current square...
      for (Square adjSquare : grid.getAdjacentSquares(currentSquare))
      {
        // (i)   Ignore if unreachable or in closed list
        if (adjSquare == null || !adjSquare.isOccupiable()
            || closedList.contains(adjSquare))
          continue;
        // (ii)  If it isn't on the open list,
        if (!openList.contains(adjSquare))
        {
          // * Add it to the open list.
          openList.add(adjSquare);
          System.out.println("Adding " + adjSquare + " to the open list.");
          // * Calculate F, G, H score.
          if (adjSquare.hasParent())
            gScore = currentSquare.getMoveCost(adjSquare) + adjSquare.getParent().getGScore();
          else
            gScore = currentSquare.getMoveCost(adjSquare);
          adjSquare.setGScore(gScore);
          adjSquare.setHScore(grid.manhattan(currentSquare, goalSquare));
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
          if (currentSquare.getGScore() + currentSquare.getMoveCost(adjSquare) < adjSquare.getGScore())
          {
            adjSquare.setParent(currentSquare);
            gScore = currentSquare.getMoveCost(adjSquare) + adjSquare.getParent().getGScore();
            adjSquare.setGScore(gScore);
            adjSquare.setHScore(grid.manhattan(currentSquare, goalSquare));
          }
        }
      }
    }
    // 3. If path has been found, calculate the path
    //   (i)   Working backwards from the target square, go from each square to its
    //         parent square until you reach the starting square.
    LinkedList<Square> pathList = new LinkedList<Square>();
    if (pathFound)
    {
      pathList.push(goalSquare);
      Square nextSquare = goalSquare.getParent();
      while (nextSquare != originSquare)
      {
        pathList.push(nextSquare);
        nextSquare = nextSquare.getParent();
      }
    }
    return pathList;
  }
}
