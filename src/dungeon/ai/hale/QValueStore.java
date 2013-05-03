package dungeon.ai.hale;

import java.util.*;
import java.io.*;

/**
 * Data structure for representing the QTable for reinforcement learning.
 * QTable uses a HashMap to associate action-state pairs with q values.
 */
public class QValueStore
{
  private HashMap<ActionState, Double> fStore;
  private static final String FILEPATH = "src/dungeon/ai/hale/QValueStore.ser";

  /**
   * By default try to load the fStore from disk.
   */
  public QValueStore()
  {
    loadFromDisk();
  }

  /**
   * Returns the q value for a given state and action.
   *
   * @param state state.
   * @param action action.
   * @return The q value for the state and action
   */
  protected double getQValue(State state, Action action)
  {
    Double value = fStore.get(new ActionState(state, action));
    return (value == null) ? 0.0 : value;
  }

  /**
   * Associates a q value with a state-action pair.
   *
   * @param state state.
   * @param action action.
   * @param value value.
   */
  protected void storeQValue(State state, Action action, double value)
  {
    ActionState key = new ActionState(state, action);
    fStore.put(key, value);
  }

  /**
   * Returns the best action for a given state.  This is the useful part of the
   * q-learning algorithm.  The best action is the action with the highest
   * q-value for the given action-state pair.
   *
   * @param state The state for which we want the best action.
   * @return The optimal action for the given state.
   */
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

  /**
   * Writes the fStore q table to a flat file, defined in FILEPATH. Does
   * nothing if an exception is thrown.
   */
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

  /**
   * Reads the fStore q table from a flat file, defined in FILEPATH. If an
   * exception is thrown, a new fStore is initialised.
   */
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

}
