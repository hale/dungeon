package dungeon.ai.pathfind;
import java.awt.geom.Point2D;

public class Square {

  public Square()
  {
    this(0,0,0,0);
  }

  public Square(int gridPointX, int gridPointY, int gScore, int hScore)
  {
    this.gridPointX = gridPointX;
    this.gridPointY = gridPointY;
    this.gScore = gScore;
    this.hScore = hScore;
    this.fCost = hScore + gScore;
  }

  public Square(Point2D origin)
  {
    this.gridPointX = planeToGrid(origin.getX());
    this.gridPointY = planeToGrid(origin.getY());
  }

  private int planeToGrid(double point)
  {
    return (int) Math.floor(point / 5.0);
  }

  private boolean occupiable;
  public boolean isOccupiable() { return this.occupiable; }
  public void setOccupiable(boolean occupiable) { this.occupiable = occupiable; }

  private int gridPointX;
  public int getGridPointX() { return gridPointX; }
  public void setGridPointX(int index) { this.gridPointX = index; }

  private int gridPointY;
  public int getGridPointY() { return gridPointY; }
  public void setGridPointY(int index) { this.gridPointY = index; }

  private int fCost;
  public int getFCost() { return fCost; }
  public void setFCost(int fCost) { this.fCost = fCost; }

  private int gScore;
  public int getGScore() { return gScore; }
  public void setGScore(int gScore) { this.gScore = gScore; }

  private int hScore;
  public int getHScore() { return hScore; }
  public void setHScore(int hScore) { this.hScore = hScore; }


}
