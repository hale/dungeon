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
  Creature fLeader;
  Game fGame;
  Random fRandom = new Random();
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

    /*
     * 1. do a path find with:
     *     originList: all the mobs in this faction
     *     goalList: all the treasure and enemies
     * 2. set the creature in the first square of that to be the leader
     * 3. set the fGoal to be the last square.
     */

    ArrayList<Item> goals = getTreasureAndEnemies();
    ArrayDeque<Point2D> path = fPathFind.findPath(new ArrayList<Item>(fCreatures), goals);
    MapPanel.setPath(new ArrayList<Point2D>(path));

    if (path.size() > 1)
    {
      //for (Point2D pt : path)
        //App.log(pt.toString());
      //fGrid.printSquares(new ArrayList<Point2D>(path), path.getFirst(), path.getLast(), fGame);

      fLeader = closestCreature(path.getLast());
      // FIXME: the problem is the path contains wrong squares at beginning and/or end.  Print the path
      assert(fLeader!=null);

      fGoal = path.getFirst();
    }
      setCreatureGoals();


    //if (fGoal == null || goalReached())
      //fGoal = newGoal();

    //if (fGoal != null)

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
    //Creature leader = closestCreatureToGoal();
    CreatureBehaviour behaviour;
    for (Creature creature : fCreatures)
    {
      //if (creature.getGoal() !=null) continue;
      behaviour = (CreatureBehaviour) creature.getBehaviour();
      //if (creature.equals(fLeader))
        behaviour.setDest(fGoal);
      //else
        //behaviour.setDest(fLeader.getLocation());
    }
  }

  private Creature closestCreature(Point2D pnt)
  {
    Creature closest = fCreatures.get(0);
    double smallest = Double.MAX_VALUE;
    for (Creature creature : fCreatures)
    {
      if (creature.getLocation().distance(closest.getLocation()) < smallest)
        closest = creature;
    }
    return closest;

  }

  //private Creature closestCreatureToGoal()
  //{
    //int lowestPathSize = Integer.MAX_VALUE;
    //assert(!fCreatures.isEmpty());
    //Creature closestCreature = fCreatures.get(0);
    //for (Creature creature : fCreatures)
    //{
      //int pathSize = (fPathFind.findPath(creature.getLocation(), fGoal)).size();
      //if (pathSize < lowestPathSize)
      //{
        //closestCreature = creature;
        //lowestPathSize = pathSize;
      //}
    //}
    //return closestCreature;
  //}

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

  private Point2D newGoal()
  {
    Point2D goal_pt = null;
    if (goal_pt == null)
      goal_pt = treasureLocation();
    if (goal_pt  == null)
      goal_pt = enemyLocation();
    //if (goal_pt  == null)
      //goal_pt  = randomLocation();
    return goal_pt;
  }

  private Point2D enemyLocation()
  {
    for (Creature creature : fGame.getCreatures())
      if (!fCreatures.contains(creature))
        return creature.getLocation();
    return null;
  }

  private boolean goalReached()
  {
    Square goalSquare = fGrid.squareAt(fGoal);
    for (Creature creature : fCreatures)
    {
      Square square = fGrid.squareAt(creature.getLocation());
      if (square.equals(goalSquare))
        return true;
    }
    return false;
  }

  private Point2D randomLocation()
  {
    Rectangle2D bounds = fGame.getMap().getBounds(0);

    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
  }

  private Point2D treasureLocation()
  {
    if (fGame.getTreasure().isEmpty()) { return null; }
    // FIXME: maybe this is bad....
    Collections.shuffle(fGame.getTreasure());
    return fGame.getTreasure().get(0).getLocation();
  }

  private Point2D heroLocation()
  {
    if (fGame.getHero() == null) { return null; }
    return fGame.getHero().getLocation();
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

