package dungeon.ai.hale;

import java.util.*;
import java.io.*;

public class QValueStore
{
  private HashMap<Object[], Double> store;
  private static final String FILEPATH = "QValueStore.ser";

  protected double getQValue(State state, Action action)
  {
    return store.get(new Object[] { state, action });
  }

  protected Action getBestAction(State state)
  {
    double maxQValue = 0.0;
    Action bestAction = null;
    for (Action action : Action.values())
    {
      double thisQValue = getQValue( state, action );
      if (thisQValue > maxQValue)
      {
        bestAction = action;
        maxQValue = thisQValue;
      }
    }
    return bestAction;
  }

  protected void storeQValue(State state, Action action, double value)
  {
    store.put(new Object[] { state, action }, value);
  }

  public void saveToDisk()
  {
    try{
      ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(FILEPATH));
      oos.writeObject( store );
      oos.flush();
      oos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void loadFromDisk()
  {
    try{
      ObjectInputStream ois = new ObjectInputStream(
          new FileInputStream(FILEPATH));
      store = (HashMap<Object[], Double>) ois.readObject();
      ois.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
