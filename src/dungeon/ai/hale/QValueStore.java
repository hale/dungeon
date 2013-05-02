package dungeon.ai.hale;

import java.util.*;
import java.io.*;

public class QValueStore
{
  private HashMap<Object[], Double> fStore;
  private static final String FILEPATH = "src/dungeon/ai/hale/QValueStore.ser";

  public QValueStore()
  {
    loadFromDisk();
    //fStore = new HashMap<Object[], Double>();
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
      sb.append("(" + actionState[0] + ") " + actionState[1] +
          " " + fStore.get(actionState) + "\n"
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
      System.out.println("Loaded the following q-table from disk:");
      System.out.println( this.toString() );
    } catch (EOFException e) {
      System.out.println("No object in the file.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      fStore = new HashMap<Object[], Double>();
    }
  }


}
