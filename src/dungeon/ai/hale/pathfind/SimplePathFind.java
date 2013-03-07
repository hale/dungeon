package dungeon.ai.hale.pathfind;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;

import dungeon.App;
import dungeon.ai.*;
import dungeon.ai.hale.pathfind.*;
import dungeon.model.*;
import dungeon.model.items.mobs.Creature;
import dungeon.utils.*;
import dungeon.ui.MapPanel;

public class SimplePathFind {

  // hash map from [sq1, sq2] to list<square>


  Grid fGrid;
  LinkedList<Square> openList = new LinkedList<Square>();
  ArrayDeque<Square> closedList = new ArrayDeque<Square>();


  public SimplePathFind(Game game, Grid grid)
  {
    this.fGrid = grid;
  }

  public ArrayDeque<Point2D> findPath(Point2D pointA, Point2D pointB)
  {
    Square originSquare = new Square(pointA);
    Square goalSquare = new Square(pointB);
    return squaresToPoints(findPath(originSquare, goalSquare));
  }

  private ArrayDeque<Point2D> squaresToPoints(ArrayDeque<Square> squares)
  {
    ArrayDeque<Point2D> points = new ArrayDeque<Point2D>();
    for (Square square : squares)
      points.add(square.getCenter());
    return points;
  }

  private ArrayDeque<Square> findPath(Square originSquare, Square goalSquare)
  {
    long startTime = System.nanoTime();

    openList.clear();
    closedList.clear();
    boolean pathFound = false;
    int gScore;
    int hScore;

    openList.add(originSquare);
    while (!openList.isEmpty() && !pathFound)
    {
        System.out.println("Open list size: " + openList.size());
      //Square currentSquare = bestSquare()
      Collections.sort(openList,
        new Comparator<Square>() {
          public int compare(Square a, Square b) {
            return a.getFCost() - b.getFCost();
          }
        }
      );
      Square currentSquare = openList.poll();
      assert(currentSquare != null);

      if (currentSquare.equals(goalSquare))
        pathFound = true;

      closedList.add(currentSquare);
      for (Square adjSquare : fGrid.getAdjacentSquares(currentSquare))
      {
        assert(adjSquare != null);
        if ( closedList.contains(adjSquare) )
          continue;
        if (!openList.contains(adjSquare))
        {
          openList.add(adjSquare);
          if (adjSquare.hasParent())
            gScore = currentSquare.getMoveCost(adjSquare) + currentSquare.getGScore();
          else
            gScore = currentSquare.getMoveCost(adjSquare);
          adjSquare.setGScore(gScore);
          adjSquare.setHScore(fGrid.chebyshevDist(currentSquare, goalSquare));
          adjSquare.setParent(currentSquare);
        } else
        {
          if (currentSquare.getGScore() + currentSquare.getMoveCost(adjSquare) < adjSquare.getGScore())
          {
            adjSquare.setParent(currentSquare);
            gScore = currentSquare.getMoveCost(adjSquare) + adjSquare.getParent().getGScore();
            adjSquare.setGScore(gScore);
            adjSquare.setHScore(fGrid.chebyshevDist(currentSquare, goalSquare));
          }
        }
        assert(adjSquare.hasParent());
      }
    }
    originSquare.setParent(null);
    ArrayDeque<Square> pathList = new ArrayDeque<Square>();
    if (pathFound)
      for (Square sq = closedList.removeLast(); !sq.equals(originSquare); sq = sq.getParent())
        pathList.push(sq);

    long ms = (System.nanoTime() - startTime) / 1000000;
    if (pathFound)
    {
      System.out.println("\033[32m Path found! \033[0m");
      System.out.println("Path is " + pathList.size() + " steps long.");
      System.out.println("Path took " + ms + " milliseconds to calculate.");
    }
    else
    {
      System.out.println("\033[31m No path found! \033[0m");
      System.out.println("Path took " + ms + " milliseconds to calculate.");
    }
    return pathList;
  }


}
