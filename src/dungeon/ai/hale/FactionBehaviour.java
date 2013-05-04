package dungeon.ai.hale;

import dungeon.ai.Behaviour;
import dungeon.ai.CollisionDetection;
import dungeon.ai.hale.pathfind.*;
import dungeon.ai.hale.qlearning.*;
import dungeon.collections.CreatureList;
import dungeon.model.Game;
import dungeon.model.items.Item;
import dungeon.model.items.mobs.Creature;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Ogre;
import dungeon.model.items.treasure.Treasure;

import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;


/**
 * Controls the overall destination for creatures in the same faction.
 */
public class FactionBehaviour implements Behaviour {

  QValueStore fQTable = new QValueStore();
  AStar fPathFind;
  Grid fGrid;
  Faction faction;
  CreatureList fCreatures;
  Game fGame;
  Point2D fGoal = null;

  /**
   * Factions are specified in the XML file as strings. E.g. "red".
   */
  public FactionBehaviour(Faction faction)
  {
    this.faction = faction;
    fCreatures = new CreatureList();
  }

  @Override
    /**
     * Is called every time in the game loop.
     *
     * On the first tick, class variables  are intialised.  This allows us to
     * write behaviour classes without always passing a Game and Creature object
     * as parameters.
     *
     * On every subsequent tick, the following happens:
     *
     * 1. The grid is updated to reflect the game state.
     * 2. Creatures in this faction are re-included.
     * 3. A new goal point is set.
     * 4. The creatures in the faction are set this goal point as their goal point.
     *
     */
    public boolean onTick(Game game)
    {
      if (fGame == null)
      {
        fGame = game;
        fGrid = new Grid(fGame);
        fPathFind = new AStar(fGrid);
        setupCreatures();
      }
      fGrid.updateGrid(fGame);
      this.fCreatures = getFactionCreatures();
      setNewGoal();
      setCreatureGoals();

      return true;
    }

  /**
   * Determines the best goal point for this faction.
   *
   * The best goal point is the shortest path between any member of this faction
   * and any reachable item.
   *
   * Setting the goal in this way has a number of useful outcomes.
   *
   * 1. Faction creatures will converge over time.
   * 2. Since there is usually more treasure than enemies, treasure is usually
   *    prioritised over enemies.
   * 3. When enemies come close, they are attacked even if there is free treasure.
   * 4. Creatures rare;y get stuck.
   */
  private void setNewGoal()
  {
    ArrayList<Item> goals = getTreasureAndEnemies();
    ArrayDeque<Point2D> path = fPathFind.findPath(new ArrayList<Item>(fCreatures), goals);
    if (path.size() > 0)
      fGoal = path.getFirst();
  }

  /**
   * Finds treasure that can be reached by an ogre, and creatures not in this faction.
   */
  private ArrayList<Item> getTreasureAndEnemies()
  {
    ArrayList<Item> items = new ArrayList<Item>(fGame.getTreasure().size() + fGame.getCreatures().size());
    for (Creature creature : fGame.getCreatures())
      if (!fCreatures.contains(creature))
        items.add(creature);
    for (Treasure treasure : fGame.getTreasure())
      if(CollisionDetection.canOccupy(fGame, new Ogre(), treasure.getLocation()))
        items.add(treasure);
    return items;
  }

  /**
   * Sets the destination goals of all the creatures to be the same as the faction goal
   */
  private void setCreatureGoals()
  {
    for (Creature creature : fCreatures)
      ((CreatureBehaviour) creature.getBehaviour()).setDest(fGoal);
  }

  /**
   * Populates the factions creatures from the global creature list.
   */
  private CreatureList getFactionCreatures()
  {
    CreatureList creatures = new CreatureList();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
        creatures.addElement(creature);
    return creatures;
  }

  /**
   * Initialises the creature behaviour class, passing it reusable objects and
   * setting th behaviour.
   */
  private void setupCreatures()
  {
    this.fCreatures = getFactionCreatures();
    for (Creature creature : fCreatures)
    {
      CreatureBehaviour behaviour = new CreatureBehaviour(creature);
      behaviour.setGrid(fGrid);
      behaviour.setPathFind(fPathFind);
      behaviour.setQTable(fQTable);
      creature.setBehaviour(behaviour);
    }
  }

  @Override
    public boolean deathTick(Game game) {
      // FIXME: this code never runs...
      System.out.println("Someone died.");
      return false;
    }

  @Override
    public boolean gameOverTick(Game game) {
      // FIXME: this code never runs...
      System.out.println("GAME OVER");
      return false;
    }
}

