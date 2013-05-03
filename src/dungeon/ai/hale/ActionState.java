package dungeon.ai.hale;

/**
 * Wrapper for an action-state pair, used in q-learning.
 */
public class ActionState implements java.io.Serializable
{
  private Action action;
  private State state;

  protected ActionState(State state, Action action)
  {
    this.action = action;
    this.state = state;
  }

  protected Action getAction() { return action; }
  protected State getState() { return state; }

  @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;

      ActionState that = (ActionState) object;

      if (action != null ? !action.equals(that.action) : that.action != null) return false;
      if (state != null ? !state.equals(that.state) : that.state != null) return false;

      return true;
    }

  @Override
    public int hashCode() {
      int result = action != null ? action.hashCode() : 0;
      result = 31 * result + (state != null ? state.hashCode() : 0);
      return result;
    }
}
