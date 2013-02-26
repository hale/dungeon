package dungeon.ai.pathfind;
import java.awt.geom.Point2D;

public class Square {

  public Square() { }

  public Square(Point2D location)
  {
    System.out.println("Making square from: " + location);
    this.x = planeToGrid(location.getX());
    this.x = planeToGrid(location.getY());
  }

  public int getMoveCost(Square adjSquare)
  {
    // look at the gridpointX of this and the other square.
    // if either of them are equal, the square is in the same
    // row or column and therefore directly above or below.
    // otherwise, it's diagonal.
    if (this.x != adjSquare.getX())
      if (this.y != adjSquare.getY())
          return 14;
    return 10;
  }

  private int planeToGrid(double point)
  {
    return (int) Math.floor(point / 5.0);
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
