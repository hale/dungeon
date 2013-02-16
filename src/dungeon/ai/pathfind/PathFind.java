package dungeon.ai.pathfind;

import java.awt.geom.Point2D;
import java.util.List;

import dungeon.App;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.structure.Tile;

/**
 * Instances of this class are used for pathfinding
 * (once it has been completed).
 */
public abstract class PathFind {

  Creature fCreature = null;
  boolean[][] map = null;
  boolean[][] tilemap = null;// Array of game tiles, and whether they can be occupied (true), or not (false)
  boolean mapInitialised=false;     // flag set true after first map calculation
  int xArraySize = 20;              // limits fixed for the two arrays above
  int yArraySize = 20;              // by xArraySize and yArraySize
  double tileSize = 5;              // Tiles are assumed to have a fixed size
  double halfTileSize = tileSize/2;

  public PathFind(Creature creature) {
    fCreature = creature;
    tilemap = new boolean[xArraySize][yArraySize];
    map     = new boolean[xArraySize][yArraySize];
  }

  abstract public List<Point2D> findPath(Game game, Point2D goal);

  //print the data from both map arrays
  public void printMap(Game game) {

    System.out.println("map");
    // populate array by query
    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        int printnum = 0;
        if (map[x][y])
          printnum = 1;
        System.out.print(printnum);
      }
      System.out.println("");
    }
    System.out.println("\n");
    System.out.println("tilemap");
    // populate array by query
    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        int printnum = 0;
        if (tilemap[x][y])
          printnum = 1;
        System.out.print(printnum);
      }
      System.out.println("");
    }
    System.out.println("\n");
  }

  // Figure-out which tiles can be occupied by creature
  // Populate tilemap with that data
  public void recalcMap(Game game) {
    if (!mapInitialised) {
      mapInitialised=true;

      // populate array by query
      for (int y = 0; y < yArraySize; y++) {
        for (int x = 0; x < xArraySize; x++) {
          Point2D.Double location = new Point2D.Double(halfTileSize
              + x * tileSize, halfTileSize + y * tileSize);
          Tile tile = App.getGame().getMap().getTileAt(location);
          if (tile != null) {
            tilemap[x][y] = tile.canOccupy(fCreature);
          } else {
            tilemap[x][y] = false;
          }
        }
      }
      copyarray(); // copy from tilemap into map

    }

  }

  // this method copies the tilemap array into the map array
  public void copyarray(){

    for (int y = 0; y < yArraySize; y++) {
      for (int x = 0; x < xArraySize; x++) {
        map[x][y]=tilemap[x][y];}}

  }

}
