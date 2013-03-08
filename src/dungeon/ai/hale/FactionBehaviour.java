package dungeon.ai.hale;

import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.model.items.Item;
import dungeon.model.items.treasure.Treasure;
import dungeon.collections.CreatureList;
import dungeon.ai.Behaviour;
import dungeon.ai.hale.pathfind.AStar;
import dungeon.ai.hale.pathfind.Grid;
import dungeon.ai.hale.pathfind.Square;

import java.util.Collections;
import java.util.Comparator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import dungeon.App;

import dungeon.ui.MapPanel;


public class FactionBehaviour implements Behaviour {

  AStar fPathFind;
  Grid fGrid;
  Faction faction;
  CreatureList fCreatures;
  Game fGame;
  Point2D fGoal = null;

  public FactionBehaviour(Faction faction)
  {
    this.faction = faction;
    fCreatures = new CreatureList();
  }

  @Override
  public boolean onTick(Game game)
  {
    if (fGame == null)
    {
      fGame = game;
      fGrid = new Grid(fGame);
      fPathFind = new AStar(fGame, fGrid);
      setupCreatures();
    }

    fGrid.updateGrid(fGame);

    this.fCreatures = getFactionCreatures();

    ArrayList<Item> goals = getTreasureAndEnemies();
    ArrayDeque<Point2D> path = fPathFind.findPath(new ArrayList<Item>(fCreatures), goals);
    MapPanel.setPath(new ArrayList<Point2D>(path));

    if (path.size() > 1)
      fGoal = path.getFirst();

    setCreatureGoals();

    return true;
  }

  private ArrayList<Item> getTreasureAndEnemies()
  {
    ArrayList<Item> items = new ArrayList<Item>(fGame.getTreasure().size() + fGame.getCreatures().size());
    for (Creature creature : fGame.getCreatures())
      if (!fCreatures.contains(creature))
        items.add((Item) creature);
    for (Treasure treasure : fGame.getTreasure())
      items.add((Item) treasure);
    return items;
  }

  private void setCreatureGoals()
  {
    CreatureBehaviour behaviour;
    for (Creature creature : fCreatures)
    {
      behaviour = (CreatureBehaviour) creature.getBehaviour();
      behaviour.setDest(fGoal);
    }
  }

  private CreatureList getFactionCreatures()
  {
    CreatureList creatures = new CreatureList();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
        creatures.addElement(creature);
    return creatures;
  }

  private void setupCreatures()
  {
    this.fCreatures = getFactionCreatures();
    for (Creature creature : fCreatures)
    {
      CreatureBehaviour behaviour = new CreatureBehaviour(creature);
      behaviour.setGrid(fGrid);
      behaviour.setPathFind(fPathFind);
      creature.setBehaviour(behaviour);
    }
  }

  @Override
    public boolean deathTick(Game game) {
      return false;
    }

  @Override
    public boolean gameOverTick(Game game) {
      return false;
    }
}

