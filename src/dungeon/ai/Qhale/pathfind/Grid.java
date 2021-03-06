package dungeon.ai.Qhale.pathfind;

import dungeon.App;
import dungeon.model.Game;
import dungeon.model.items.treasure.Treasure;
import dungeon.model.structure.Door;
import dungeon.model.structure.FlameTrap;
import dungeon.model.structure.Pit;
import dungeon.model.structure.Tile;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Grid representation of the map, for use in pathfinding.
 */
public class Grid {

  /**
   * The dimensions of a Square in the grid
   */
  protected static final int TILE_SIZE = 5;
  double halfTileSize = (double) TILE_SIZE/2;

  /**
   * The height and width of the 2D array
   */
  int xArraySize;
  int yArraySize;

  /**
   * 2D array of all the squares in the game
   */
  Square[][] sqGrid;

  /**
   * Picks the dimensions of the grid based on the tile size, to support
   * variable size maps
   */
  public Grid(Game game)
  {
    Rectangle2D bounds = game.getMap().getBounds(0);
    this.xArraySize = (int) (bounds.getWidth() / TILE_SIZE);
    this.yArraySize = (int) (bounds.getHeight() / TILE_SIZE);
    sqGrid =  new Square[xArraySize][yArraySize];
    constructGrid();
    updateGrid(game);
  }

  /**
   * The square object at a given [x,y] grid coordinate
   *
   * @return The square at this x,y
   */
  protected Square squareAt(int x, int y)
  {
    return sqGrid[x][y];
  }

  /**
   * The square containing a point on the plane
   *
   * @param point a 2D point on the plane
   * @return the Square, an approximatino of this location.
   */
  public Square squareAt(Point2D point)
  {
    int x = (int) (point.getX() / 5.0);
    int y = (int) (point.getY() / 5.0);
    return squareAt(x, y);
  }

  /**
   * The set of squares that contain treasure.
   *
   * @param game The game object to get treasure from.
   * @return A subset of treasure in the game.
   */
  public HashSet<Square> getTreasureSquares(Game game)
  {
    HashSet<Square> treasureSquares = new HashSet<Square>();
    for (Treasure treasure : game.getTreasure())
       treasureSquares.add(new Square(treasure.getLocation()));
    return treasureSquares;
  }

  /**
   * A treasure item in a given square.  If there is more than one treasure item,
   * the treasure returned is arbitarily chosen.
   *
   * @param square Where to look for treasure.
   * @param game The game to get treasure from.
   *
   * @return A Treasure object within this square.
   */
  public Treasure getTreasureIn(Square square, Game game)
  {
    Treasure treasure = null;
    for (Treasure t: game.getTreasure())
      if (square.equals(new Square(t.getLocation())))
        treasure = t;
    return treasure;
  }

  /**
   * Updates changeable attributes of (squares in) the grid. The current
   * implementation only tracks treasure.
   * @param game The game state to inspect.
   */
  public void updateGrid(Game game)
  {
    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        Square sq = squareAt(x, y);
        if (getTreasureSquares(game).contains(sq))
          sq.setContainsTreasure( true );
        else
          sq.setContainsTreasure( false );
        //for (Creature creature : game.getCreatures())
          //if (creature.getShape().intersects(sq.getRectangle()))
            //sq.setOccupiable( false );
      }
    }
  }

  /**
   * Squares in the grid one-step away from a given square.
   *
   * The grid currently assumes diagonal movement is allowed.  Only reachable
   * squares are returned. A reachable square:
   *
   *   1. is truly adjacent (ie not the same square)
   *   2. is not out of bounds of the grid
   *   3. is occupiable (not a wall)
   *   4. can be reached (does not intersect a wall)
   *
   * @param square The center square.
   * @return Between 0 and 8 squares.
   */
  public List<Square> getAdjacentSquares(Square square)
  {
    List<Square> adjSquares = new ArrayList<Square>(9);

    int sqX = square.getX();
    int sqY = square.getY();

    int maxX = xArraySize -1;
    int maxY = yArraySize -1;

    boolean up,right,down,left;
    up = right = down = left = true;

    // above blocked
    if (sqY == maxY) up = false;
    // below blocked
    if (sqY == 0) down = false;
    // right blocked
    if (sqX == maxX) right = false;
    // left blocked
    if (sqX == 0) left = false;

    /* UPPER ROW */
    if (up && left)
      if (sqGrid[ sqX ][ sqY + 1].isOccupiable())
        if (sqGrid[ sqX-1 ][ sqY ].isOccupiable())
          addSquareIfOccupiable( sqGrid[ sqX - 1 ][ sqY + 1], adjSquares);
    if (up)
      addSquareIfOccupiable( sqGrid[ sqX     ][ sqY + 1], adjSquares);
    if (up && right)
      if (sqGrid[ sqX ][ sqY + 1].isOccupiable())
        if (sqGrid[ sqX+1 ][ sqY ].isOccupiable())
          addSquareIfOccupiable( sqGrid[ sqX + 1 ][ sqY + 1], adjSquares);

    /* CENTER ROW */
    if (left)
      addSquareIfOccupiable( sqGrid[ sqX - 1 ][ sqY    ], adjSquares);
    if (right)
      addSquareIfOccupiable( sqGrid[ sqX + 1 ][ sqY    ], adjSquares);

    /* LOWER ROW */
    if (down && left)
      if( sqGrid[ sqX ][ sqY-1 ].isOccupiable() )
        if( sqGrid[ sqX-1 ][ sqY-1 ].isOccupiable() )
          addSquareIfOccupiable( sqGrid[ sqX - 1 ][ sqY    ], adjSquares);
    if (down)
      addSquareIfOccupiable( sqGrid[ sqX     ][ sqY - 1], adjSquares);
    if (down && right)
      if( sqGrid[ sqX     ][ sqY - 1].isOccupiable() )
        if( sqGrid[ sqX + 1 ][ sqY    ].isOccupiable() )
          addSquareIfOccupiable( sqGrid[ sqX + 1 ][ sqY - 1], adjSquares);

    return adjSquares;
  }
  private void addSquareIfOccupiable(Square square, List<Square> list)
  {
    if (square.isOccupiable())
      list.add(square);
  }

  /**
   * The rectillinear distance between two squares. This heuristic function
   * results in an inadmissable A*, since diagonal movement is allowed.
   *
   * @param sq1
   * @param sq2
   * @return An integer distance; a multiple of 144.
   */
  public int manhattanDist(Square sq1, Square sq2)
  {
    int xDist = Math.abs(sq1.getX() - sq2.getX());
    int yDist = Math.abs(sq1.getY() - sq2.getY());
    return 10*(xDist + yDist);
  }

  /**
   * The diagonal distance between two squares.  This heuristic function is
   * admissable and a better approximation than euclidian distance.
   *
   * @param sq1
   * @param sq2
   * @return An integer distance; a multiple of 144.
   */
  public int chebyshevDist(Square sq1, Square sq2)
  {
    int xDist = Math.abs(sq1.getX() - sq2.getX());
    int yDist = Math.abs(sq1.getY() - sq2.getY());
    if (xDist > yDist)
      return (14 * yDist) + (10 * (xDist - yDist));
    else
      return (14 * xDist) + (10 * (yDist - xDist));
  }

  /**
   * Prints a colourised representation of the grid to stdout.  Will only work in
   * terminal emulators that support UNIX escape codes.
   *
   * @param list A list of points to plot.
   * @param p1 The origin point.
   * @param p2 The goal point.
   */
  public void printSquares(List<Point2D> list, Point2D p1, Point2D p2, Game game)
  {
    Square origin = squareAt(p1);
    Square goal = squareAt(p2);

    System.out.println(" == MAP SIZE: "
        + (xArraySize*TILE_SIZE) + "x" + (yArraySize*TILE_SIZE) + " == ");
    for (int y = 0; y < yArraySize ; y++) {
      for (int x = 0; x < xArraySize ; x++) {
        String printChar = "\033[31m" + "0" + "\033[0m";
        if (sqGrid[x][y].isOccupiable())
          printChar = "1";
        if (sqGrid[x][y].getTerrainCost() > 0)
          printChar = "\033[35m" + "1" + "\033[0m";
        if (getTreasureSquares(game).contains(sqGrid[x][y]))
          printChar = "\033[33m" + "T" + "\033[0m";
        if (list.contains(sqGrid[x][y].getCenter()))
          printChar = "\033[32m" + "X" + "\033[0m";
        if (sqGrid[x][y].equals(origin))
          printChar = "\033[34m" + "O" + "\033[0m";
        if (sqGrid[x][y].equals(goal))
          printChar = "\033[34m" + "G" + "\033[0m";
        System.out.print(printChar);
      }
      System.out.println();
    }
  }

  /**
   * Populates the grid with squares, and sets those squares based on the game
   * state.
   *
   * Pits, closed doors and the gaps between tiles are set as non-occupiable.
   *
   * Flame traps are given a positive terrain modifier.  This causes the
   * pathfinding algorithm to avoid flame traps unless no other path is possible.
   *
   */
  private void constructGrid()
  {
    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        Square square = new Square();
        Tile tile = getTileAtGrid(x, y);
        square.setX(x);
        square.setY(y);

        if (tile == null || isPit(tile) || isClosedDoor(tile) )
          square.setOccupiable( false );

        if (tile !=null && isTrap(tile))
          square.setTerrainCost(4000);

        sqGrid[x][y] = square;
      }
    }
  }

  private boolean isClosedDoor(Tile tile)
  {
    Door door;
    try{
      door = (Door) tile;
    } catch(ClassCastException e) {
      return false;
    }
    if (door.getState() == Door.CLOSED || door.getState() == Door.LOCKED)
      return true;
    return false;
  }
  private boolean isTrap(Tile tile)
  {
    try{
      FlameTrap trap = (FlameTrap) tile;
    } catch(ClassCastException e) {
      return false;
    }
    return true;
  }
  private boolean isPit(Tile tile)
  {
    try{
      Pit pit = (Pit) tile;
    } catch(ClassCastException e) {
      return false;
    }
    return true;
  }

  /**
   * translates an x,y grid reference to a grid coordinate, and gets the tile
   * at that point.
   */
  private Tile getTileAtGrid(int x, int y)
  {
    Point2D.Double location = new Point2D.Double(halfTileSize
         + x * TILE_SIZE, halfTileSize + y * TILE_SIZE);

     return App.getGame().getMap().getTileAt(location);
   }

}
