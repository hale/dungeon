package dungeon.ai.hale;

import java.util.*;

/**
 * Identify the action a creature can take.
 */
public enum Action
{
  WAIT, MOVE_TO_GOAL;

  /**
   * @return Random action from the above list.
   */
  public static Action random()
  {
    if ( new Random().nextInt(2) == 0)
     return Action.WAIT;
    else
      return Action.MOVE_TO_GOAL;
  }
}
