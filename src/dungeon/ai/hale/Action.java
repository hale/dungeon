package dungeon.ai.hale;

import java.util.*;

public enum Action
{
  WAIT, MOVE_TO_GOAL;

  public static Action random()
  {
    if ( new Random().nextInt(2) == 0)
     return Action.WAIT;
    else
      return Action.MOVE_TO_GOAL;
  }
}
