package dungeon.ai.hale;

public class State implements java.io.Serializable {
  private int energy;
  private int health;

  protected static final int MAX_ENERGY = 5;
  protected static final int MAX_HEALTH = 5;
  private boolean isThreatened;

  public State()
  {
    this(5, 5, false);
  }

  public State(int energy, int health, boolean isThreatened)
  {
    this.energy = energy;
    this.health = health;
    this.isThreatened = isThreatened;
  }

  public int getEnergy() { return energy; }
  public int getHealth() { return health; }
  public boolean isThreatened() { return isThreatened; }
  public void setEnergy(int energy) { this.energy = energy; }
  public void setHealth(int health) { this.health = health; }
  public void setThreatened(boolean threatened) { this.isThreatened = threatened; }

  @Override
  public String toString()
  {
    String threatStr = isThreatened ? "THREATENED" : "SAFE";
    return "E" + energy + " H" + health + " " + threatStr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    State state = (State) o;

    if (energy != state.energy) return false;
    if (health != state.health) return false;
    if (isThreatened != state.isThreatened) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = energy;
    result = 31 * result + health;
    result = 31 * result + (isThreatened ? 1 : 0);
    return result;
  }
}
