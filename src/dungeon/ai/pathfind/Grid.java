package dungeon.ai.pathfind;

import dungeon.App;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.structure.Tile;

import java.awt.geom.Point2D;

/**
 * Grid representation of the map, for use in pathfinding.
 */
public class Grid {

  boolean[][] grid = null;
  boolean[][] tilegrid = null;// Array of game tiles, and whether they can be occupied (true), or not (false)
  boolean gridInitialised =false;     // flag set true after first grid calculation
  int xArraySize = 20;              // limits fixed for the two arrays above
  int yArraySize = 20;              // by xArraySize and yArraySize
  double tileSize = 5;              // Tiles are assumed to have a fixed size
  double halfTileSize = tileSize/2;

  public Grid()
  {
    tilegrid = new boolean[xArraySize][yArraySize];
    grid     = new boolean[xArraySize][yArraySize];
  }

  /**
   * Takes Creature paramater because occupiable is specific to an individual creature
   */
  public Grid(Creature fCreature, Game game)
  {
    this();
    recalcGrid(fCreature, game);
  }

  //print the data from both grid arrays
  public void print(Game game) {

    System.out.println("grid");
    // populate array by query
    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        int printnum = 0;
        if (grid[x][y])
          printnum = 1;
        System.out.print(printnum);
      }
      System.out.println("");
    }
    System.out.println("\n");
    System.out.println("tilegrid");
    // populate array by query
    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        int printnum = 0;
        if (tilegrid[x][y])
          printnum = 1;
        System.out.print(printnum);
      }
      System.out.println("");
    }
    System.out.println("\n");
  }

  // Figure-out which tiles can be occupied by creature
  // Populate tilegrid with that data
  public void recalcGrid(Creature fCreature, Game game) {
    if (!gridInitialised) {
      gridInitialised=true;

      // populate array by query
          System.out.println("Location\t\t\tX\tY\tOccupiable? ");
      for (int y = 0; y < yArraySize; y++) {
        for (int x = 0; x < xArraySize; x++) {
          Point2D.Double location = new Point2D.Double(halfTileSize
              + x * tileSize, halfTileSize + y * tileSize);
          Tile tile = App.getGame().getMap().getTileAt(location);
          if (tile != null) {
            tilegrid[x][y] = tile.canOccupy(fCreature);
          } else {
            tilegrid[x][y] = false;
          }
          System.out.print(location + "\t");
          System.out.print(x + "\t");
          System.out.print(y + "\t");
          System.out.print(tilegrid[x][y]);
          System.out.println();
        }
      }
      copyarray(); // copy from tilegrid into grid
    }
  }

  // this method copies the tilegrid array into the grid array
  public void copyarray(){

    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        grid[x][y]=tilegrid[x][y];}}

  }
}
