package dungeon.ai.hale;

import java.util.*;
import java.io.*;

public class QValueStore
{
  private HashMap<Object[], Double> fStore;
  private static final String FILEPATH = "QValueStore.ser";

  public QValueStore()
  {
    //loadFromDisk();
    fStore = new HashMap<Object[], Double>();
  }

  protected double getQValue(State state, Action action)
  {
    Double value = fStore.get(new Object[] { state, action });
    return (value == null) ? 0.0 : value;
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
    fStore.put(new Object[] { state, action }, value);
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(1000);
    for (Object[] actionState : fStore.keySet())
    {
      sb.append("S(" + actionState[0] + ") A(" + actionState[1] +
          ") Q(" + fStore.get(actionState) + ")\n"
      );
    }
    return sb.toString();
  }

  public void saveToDisk()
  {
    try{
      ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(FILEPATH));
      oos.writeObject( fStore );
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
      fStore = (HashMap<Object[], Double>) ois.readObject();
      ois.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
