package dungeon.ai.pathfind;

import dungeon.App;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.structure.Tile;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Grid representation of the map, for use in pathfinding.
 */
public class Grid {

  boolean gridInitialised =false;
  int xArraySize = 20;
  int yArraySize = 20;
  double tileSize = 5;
  double halfTileSize = tileSize/2;

  Square[][] sqGrid = new Square[xArraySize][yArraySize];

  public Grid(Creature fCreature, Game game)
  {
    constructGrid(fCreature, game);
  }

  private void constructGrid(Creature fCreature, Game game)
  {
    if (!gridInitialised) {
      gridInitialised=true;

      for (int y = 0; y < yArraySize; y++) {
        for (int x = 0; x < xArraySize; x++) {
          sqGrid[x][y] = new Square();
          sqGrid[x][y].setX(x);
          sqGrid[x][y].setY(y);

          Point2D.Double location = new Point2D.Double(halfTileSize
              + x * tileSize, halfTileSize + y * tileSize);

          Tile tile = App.getGame().getMap().getTileAt(location);
          if (tile != null) {
            sqGrid[x][y].setOccupiable( tile.canOccupy(fCreature) );
          } else {
            sqGrid[x][y].setOccupiable( false );
          }
        }
      }
    }
  }
  // there can be up to 8 adjacent squares; there may be less.
  public Square[] getAdjacentSquares(Square square)
  {
    //System.out.print("Getting adjacent squares for: [");
    //System.out.println(square.getX() + "," + square.getY() + "]");

    // TODO: Rewrite this method to only return adjacent squares
    // which are valid.  A valid adjacent square:
    // 1) is truly adjacent (ie not the same square)
    // 2) is not out of bounds of the grid
    // 3) is occupiable (not a wall)
    // 4) can be reached (does not intersect a wall)
    // TODO: remove isOccupiable() check in adjSquares loop

    Square[] adjSquares = new Square[8];

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
      // if square above occupiable and square left occupiable
      adjSquares[0] = sqGrid[ sqX - 1 ][ sqY + 1];
    if (up)
      adjSquares[1] = sqGrid[ sqX     ][ sqY + 1];
    if (up && right)
      adjSquares[2] = sqGrid[ sqX + 1 ][ sqY + 1];

    /* CENTER ROW */
    if (left)
      adjSquares[3] = sqGrid[ sqX - 1 ][ sqY    ];
    if (right)
      adjSquares[4] = sqGrid[ sqX + 1 ][ sqY    ];

    /* LOWER ROW */
    if (down && left)
      adjSquares[5] = sqGrid[ sqX - 1 ][ sqY - 1];
    if (down)
      adjSquares[6] = sqGrid[ sqX     ][ sqY - 1];
    if (down && right)
      adjSquares[7] = sqGrid[ sqX + 1 ][ sqY - 1];

    return adjSquares;
  }

  public int manhattan(Square sq1, Square sq2)
  {
    int xDist = Math.abs(sq1.getX() - sq2.getX());
    int yDist = Math.abs(sq1.getY() - sq2.getY());
    return 10*(xDist + yDist);
  }


  public void printSqGrid()
  {
    System.out.println("=== GRID OF SQUARES ===");
    for (int y = 0; y < yArraySize ; y++) {
      for (int x = 0; x < xArraySize ; x++) {
        int occupiable = 0;
        if (sqGrid[x][y].isOccupiable())
          occupiable = 1;
        System.out.print(occupiable);
      }
      System.out.println();
    }
  }

  public void printSquares(List<Square> list, Square origin, Square goal)
  {
    System.out.println("=== GRID OF SQUARES ===");
    for (int y = 0; y < yArraySize ; y++) {
      for (int x = 0; x < xArraySize ; x++) {
        String printChar = "\033[31m0\033[0m";
        if (sqGrid[x][y].isOccupiable())
          printChar = "1";
        if (list.contains(sqGrid[x][y]))
          printChar = "\033[32mX\033[0m";
        if (sqGrid[x][y].equals(origin))
          printChar = "\033[34mS\033[0m";
        if (sqGrid[x][y].equals(goal))
          printChar = "\033[34mF\033[0m";
        System.out.print(printChar);
      }
      System.out.println();
    }
  }

}
