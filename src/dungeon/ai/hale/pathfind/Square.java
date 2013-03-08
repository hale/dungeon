package dungeon.ai.hale.pathfind;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Square {

  public Square() { }

  public Square(Point2D location)
  {
    this.x = planeToGrid(location.getX());
    this.y = planeToGrid(location.getY());
  }
  private int x;
  public int getX() { return x; }
  public void setX(int x) { this.x = x; }

  private int y;
  public int getY() { return y; }
  public void setY(int y) { this.y = y; }

  public int getFCost() { return gScore + hScore; }

  private int gScore;
  public int getGScore() { return gScore; }
  public void setGScore(int gScore) { this.gScore = gScore; }

  private int hScore;
  public void setHScore(int hScore) { this.hScore = hScore; }

  private Square parent = null;;
  public Square getParent() { return parent; }
  public void setParent(Square parent) { this.parent = parent; }
  public boolean hasParent() { return (parent != null) ? true : false; }

  private boolean occupiable = true;
  public boolean isOccupiable() { return this.occupiable; }
  public void setOccupiable(boolean occupiable) { this.occupiable = occupiable; }

  private int terrainCost = 0;
  public int getTerrainCost() { return terrainCost; }
  public void setTerrainCost(int cost) { this.terrainCost = cost; }

  private boolean containsTreasure = false;
  public boolean containsTreasure() { return containsTreasure; }
  protected void setContainsTreasure(boolean containsTreasure) { this.containsTreasure = containsTreasure; }

  /**
   * The movement cost of a square is defined as it's terrain cost, plus a relative
   * measure of how long it takes to get from the other square.
   *
   * @param adjSquare The square to move from
   * @return terrain cost + 10 or 14 depending on the adjSquare.
   */
  public int getMoveCost(Square adjSquare)
  {
    return terrainCost + ((isDiagonal(adjSquare)) ? 14 : 10);
  }

  /**
   * Determines if this square and the other are aligned horizontally or vertically,
   * or neither.
   *
   * @param adjSquare The other square.
   * @return true if the squares are in the same row or column, false otherwise.
   */
  public boolean isDiagonal(Square adjSquare)
  {
    if (this.x != adjSquare.getX())
      if (this.y != adjSquare.getY())
        return true;
    return false;
  }

  private int planeToGrid(double point)
  {
    return (int) (point / 5.0);
  }

  /**
   * Useful for interfacing with non-grid aware parts of the game engine.
   *
   * @ return A Rectangle representatino of this square.
   */
  protected Rectangle2D getRectangle()
  {
    double height = Grid.TILE_SIZE;
    double width =  Grid.TILE_SIZE;
    double x =  ((this.x) * 5) ;
    double y =  ((this.y) * 5) ;
    return new Rectangle2D.Double(x, y, width, height);
  }


  /**
   * @return  the center of this tile
   */
  public Point2D getCenter()
  {
    return new Point2D.Double( (x*5)+2.5, (y*5)+2.5 );
  }

  @Override
    /**
     * A square is equal to another square if they have the same x and y grid
     * coordinate.
     */
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Square square = (Square) o;

    if (x != square.x) return false;
    if (y != square.y) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }


  @Override
  public String toString()
  {
    return "[" + x + "," + y + "]";
  }

}
