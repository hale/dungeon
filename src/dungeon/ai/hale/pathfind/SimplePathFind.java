package dungeon.ai.hale.pathfind;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.ArrayList;
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
  ArrayDeque<Square> closedList;
  PriorityQueue<Square> openList;


  public SimplePathFind(Game game, Grid grid)
  {
    this.fGrid = grid;
    this.closedList = new ArrayDeque<Square>();
    this.openList = new PriorityQueue<Square>(fGrid.TILE_SIZE * fGrid.TILE_SIZE,
      new Comparator<Square>() {
              public int compare(Square a, Square b) {
                return a.getFCost() - b.getFCost();
              }
            }
      );
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
    //ArrayList<Integer> openListSizes = new ArrayList<Integer>();
    long startTime = System.nanoTime();

    openList.clear();
    closedList.clear();
    boolean pathFound = false;
    int gScore;
    int hScore;

    openList.add(originSquare);
    while (!openList.isEmpty() && !pathFound)
    {
      //openListSizes.add(openList.size());
      //Square currentSquare = bestSquare()
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
          if (adjSquare.hasParent())
            gScore = currentSquare.getMoveCost(adjSquare) + currentSquare.getGScore();
          else
            gScore = currentSquare.getMoveCost(adjSquare);
          adjSquare.setGScore(gScore);
          adjSquare.setHScore(fGrid.chebyshevDist(currentSquare, goalSquare));
          adjSquare.setParent(currentSquare);
          openList.add(adjSquare);
        } else
        {
          if (currentSquare.getGScore() + currentSquare.getMoveCost(adjSquare) < adjSquare.getGScore())
          {
            adjSquare.setParent(currentSquare);
            gScore = currentSquare.getMoveCost(adjSquare) + adjSquare.getParent().getGScore();
            adjSquare.setGScore(gScore);
            adjSquare.setHScore(fGrid.chebyshevDist(currentSquare, goalSquare));
            // now remove it and add it again to the openList to ensure order in the heap.
            openList.remove(adjSquare);
            openList.add(adjSquare);
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

    //long sum = 0;
    //for (Integer score : openListSizes)
      //sum += score;
    //double average = (double) sum / openList.size();
    //System.out.println("Average openlist size: " + average);

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
