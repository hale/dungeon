package dungeon.ai.hale.pathfind;
//import dungoen.model.structure.FlameTrap;
import java.awt.geom.Point2D;

public class Square {

  public Square() { }

  public Square(Point2D location)
  {
    //System.out.println("Making square from: " + location);
    this.x = planeToGrid(location.getX());
    this.y = planeToGrid(location.getY());
    //if (game.getMap().getTileAt(location) instanceof FlamePit)
      //this.terrainCost = 10;
    //System.out.println("Square: [" + x + "," + y + "]");
  }

  public int getMoveCost(Square adjSquare)
  {
    return terrainCost + ((isDiagonal(adjSquare)) ? 14 : 10);
  }

  /* look at the gridpointX of this and the other square.
   * if either of them are equal, the square is in the same
   * row or column and therefore directly above or below.
   * otherwise, it's diagonal.
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

  private boolean occupiable = false;
  public boolean isOccupiable() { return this.occupiable; }
  public void setOccupiable(boolean occupiable) { this.occupiable = occupiable; }

  private int x;
  public int getX() { return x; }
  public void setX(int x) { this.x = x; }

  private int y;
  public int getY() { return y; }
  public void setY(int y) { this.y = y; }

  /* The cost of moving on this square.  E.g. for flame traps */
  private int terrainCost = 0;

  private int fCost;
  public int getFCost() { return gScore + hScore; }

  private int gScore;
  public int getGScore() { return gScore; }
  public void setGScore(int gScore) { this.gScore = gScore; }

  private int hScore;
  public int getHScore() { return hScore; }
  public void setHScore(int hScore) { this.hScore = hScore; }

  private Square parent = null;;
  public Square getParent() { return parent; }
  public void setParent(Square parent) { this.parent = parent; }
  public boolean hasParent() { return (parent != null) ? true : false; }

  @Override
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

  public Point2D getCenter()
  {
    return new Point2D.Double( (x*5)+2.5, (y*5)+2.5 );
  }

  @Override
  public String toString()
  {
    //return (occupiable) ? "1" : "0";
    return "[" + x + "," + y + "]";
  }

}
