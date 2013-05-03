package dungeon.ai.hale;

import java.util.*;
import java.io.*;

public class QValueStore
{
  private HashMap<ActionState, Double> fStore;
  private static final String FILEPATH = "src/dungeon/ai/hale/QValueStore.ser";

  public QValueStore()
  {
    loadFromDisk();
  }

  protected double getQValue(State state, Action action)
  {
    Double value = fStore.get(new ActionState(state, action));
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
    ActionState key = new ActionState(state, action);
    fStore.put(key, value);
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(1000);
    for (ActionState actionState : fStore.keySet())
    {
      sb.append("(" + actionState.getState() + ") " + actionState.getAction() +
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
      System.out.println("Table saved to disk");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void loadFromDisk()
  {
    try{
      ObjectInputStream ois = new ObjectInputStream(
          new FileInputStream(FILEPATH));
      fStore = (HashMap<ActionState, Double>) ois.readObject();
      ois.close();
      System.out.println("Loaded " + fStore.keySet().size() + " q values from disk");
      System.out.println( this.toString() );
    } catch (EOFException e) {
      System.out.println("No object in the file.");
      fStore = new HashMap<ActionState, Double>();
    } catch (Exception e) {
      e.printStackTrace();
      fStore = new HashMap<ActionState, Double>();
    }
  }


}
