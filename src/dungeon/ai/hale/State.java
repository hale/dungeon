package dungeon.ai.hale;

public class State implements java.io.Serializable {
  private int energy;
  private int health;

  protected static final int MAX_ENERGY = 5;
  protected static final int MAX_HEALTH = 5;
  private boolean isThreatened;
  private int pathSize;

  /**
   * Initialises the state with sensible defaults.
   */
  public State()
  {
    this(5, 5, false, 0);
  }

  /**
   * @param energy Energy level 1 to 5.
   * @param health Health level 1 to 5.
   * @param isThreatened whether the creature is under attacked.
   * @param pathSize The number of steps (squares) from creature to goal.
   */
  public State(int energy, int health, boolean isThreatened, int pathSize)
  {
    this.energy = energy;
    this.health = health;
    this.isThreatened = isThreatened;
    this.pathSize = pathSize;
  }

  public int getEnergy() { return energy; }
  public void setEnergy(int energy) { this.energy = energy; }

  public int getHealth() { return health; }
  public void setHealth(int health) { this.health = health; }

  public boolean isThreatened() { return isThreatened; }
  public void setThreatened(boolean threatened) { this.isThreatened = threatened; }

  public int getPathSize() { return pathSize; }
  public void setPathSize(int pathSize) { this.pathSize = pathSize; }

  @Override
  public String toString()
  {
    String threatStr = isThreatened ? "THREATENED" : "SAFE";
    return "E" + energy + " H" + health + " P" + pathSize + " " + threatStr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    State state = (State) o;

    if (energy != state.energy) return false;
    if (health != state.health) return false;
    if (isThreatened != state.isThreatened) return false;
    if (pathSize != state.pathSize) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = energy;
    result = 31 * result + health;
    result = 31 * result + (isThreatened ? 1 : 0);
    result = 31 * result + pathSize;
    return result;
  }
}
