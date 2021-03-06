package dungeon.ai.Qhale.pathfind;

import dungeon.model.items.Item;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * For finding paths between points using the A* algorithm.
 *
 * The openList is implemented as a heap, using Java.util.PriorityQueue.
 * Paths are saved as an ArrayDeque
 */
public class AStar {

  Grid fGrid;
  ArrayDeque<Square> closedList;
  PriorityQueue<Square> openList;

  /**
   * @param grid The grid to find paths on.
   */
  public AStar(Grid grid)
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

  /**
   * Finds the shortest path between two points on a plane.
   *
   * @param pointA The first point.
   * @param pointB The second point.
   * @return A minimal deque of adjacent points from pointA to pointB.
   */
  public ArrayDeque<Point2D> findPath(Point2D pointA, Point2D pointB)
  {
    Set<Square> origins = new HashSet<Square>();
    Set<Square> goals = new HashSet<Square>();
    origins.add(new Square(pointA));
    goals.add(new Square(pointB));
    return squaresToPoints(findShortestPath(origins, goals));
  }

  /**
   * Finds the shortest path between one list of items and another.
   *
   * @param origins A list of items.
   * @param goals A list of items.
   * @return The shortest path from any x in origins to any y in goals.
   */
  public ArrayDeque<Point2D> findPath(List<Item> origins, List<Item> goals)
  {
    Set<Square> originSquares = new HashSet<Square>();
    Set<Square> goalSquares = new HashSet<Square>();
    for (Item origin : origins)
      originSquares.add(new Square(origin.getLocation()));
    for (Item goal : goals)
      goalSquares.add(new Square(goal.getLocation()));
    return squaresToPoints(findShortestPath(originSquares, goalSquares));
  }

  /**
   * The A* algorithm.
   *
   * Initial pseudocode taken from http://www.policyalmanac.org/games/aStarTutorial.htm
   *
   * Adapted to use a PriorityQueue, and support multiple start and finish points.
   */
  private ArrayDeque<Square> findShortestPath(Set<Square> origins, Set<Square> goals)
  {

    //long startTime = System.nanoTime();

    openList.clear();
    closedList.clear();
    boolean pathFound = false;
    int gScore;

    for (Square sq : origins)
    {
      sq.setGScore(0);
      sq.setHScore(smallestChebyshev(sq, goals));
      openList.add(sq);
    }

    while (!openList.isEmpty() && !pathFound)
    {
      Square currentSquare = openList.poll();
      assert(currentSquare != null);

      for (Square goal : goals)
        if (currentSquare.equals(goal))
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
          adjSquare.setHScore(smallestChebyshev(currentSquare, goals));
          adjSquare.setParent(currentSquare);
          openList.add(adjSquare);
        } else
        {
          if (currentSquare.getGScore() + currentSquare.getMoveCost(adjSquare) < adjSquare.getGScore())
          {
            adjSquare.setParent(currentSquare);
            gScore = currentSquare.getMoveCost(adjSquare) + adjSquare.getParent().getGScore();
            adjSquare.setGScore(gScore);
            adjSquare.setHScore(smallestChebyshev(currentSquare, goals));
            // now remove it and add it again to the openList to ensure order in the heap.
            openList.remove(adjSquare);
            openList.add(adjSquare);
          }
        }
      }
    }
    for (Square origin : origins)
      origin.setParent(null);
    ArrayDeque<Square> pathList = new ArrayDeque<Square>();
    if (pathFound)
      for (Square sq = closedList.removeLast(); !origins.contains(sq); sq = sq.getParent())
        pathList.push(sq);

    //long ms = (System.nanoTime() - startTime) / 1000000;
    //if (pathFound)
    //{
      //System.out.println("\033[32m Path found! \033[0m");
      //System.out.println("Path is " + pathList.size() + " steps long.");
      //System.out.println("Path took " + ms + " milliseconds to calculate.");
    //}
    //else
    //{
      //System.out.println("\033[31m No path found! \033[0m");
      //System.out.println("Path took " + ms + " milliseconds to calculate.");
    //}
    return pathList;
  }

  private ArrayDeque<Point2D> squaresToPoints(ArrayDeque<Square> squares)
  {
    ArrayDeque<Point2D> points = new ArrayDeque<Point2D>();
    for (Square square : squares)
      points.add(square.getCenter());
    return points;
  }

  private int smallestChebyshev(Square origin, Set<Square> goals)
  {
    int smallest = Integer.MAX_VALUE;
    for (Square goal : goals)
    {
      int cheb = fGrid.chebyshevDist(origin, goal);
      if (cheb < smallest)
        smallest = cheb;
    }
    return smallest;
  }


}
