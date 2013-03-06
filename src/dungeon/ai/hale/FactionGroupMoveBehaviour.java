package dungeon.ai.hale;

import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.collections.CreatureList;
import dungeon.ai.Behaviour;
import dungeon.ai.hale.pathfind.SimplePathFind;
import dungeon.ai.hale.pathfind.Grid;

import java.util.Collections;
import java.util.Comparator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;


public class FactionGroupMoveBehaviour implements Behaviour {

  SimplePathFind fPathFind;
  Grid fGrid;
  Faction faction;
  CreatureList fCreatures;
  Creature leader;
  Game fGame;
  Random fRandom = new Random();
  Point2D fGoal = null;

  public FactionGroupMoveBehaviour(Faction faction)
  {
    this.faction = faction;
    fCreatures = new CreatureList();
  }

  @Override
  public boolean onTick(Game game)
  {
    if (fGame == null) this.fGame = game;
    if (fGrid == null) this.fGrid = new Grid(fGame);
    if (fPathFind == null) this.fPathFind = new SimplePathFind(fGame, fGrid);

    this.fCreatures = getFactionCreatures();

    // TODO: only get new goal when the faction goal has been reached
    if (fGoal == null) { this.fGoal = newGoal(); }
    //this.fGoal = newGoal();

    groupMove();

    return true;
  }

  // TODO: recursively set each creature to follow previous creature.
  private void groupMove()
  {
    // generate path for all of the creatures to this.fGoal
    // find the creature with the shortest path, set it as the leader.
    // generate path for all other creatures to creature's location
    PatientPathFindBehaviour behaviour;
    Creature leader = closestCreatureToGoal();
    for (Creature creature : fCreatures)
    {
      if (creature.getGoal() !=null) continue;
      behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
      if (creature.equals(leader))
        behaviour.setGoal(fGoal);
      else
        behaviour.setGoal(leader.getLocation());
    }
  }

  private Creature closestCreatureToGoal()
  {
    int lowestPathSize = Integer.MAX_VALUE;
    assert(!fCreatures.isEmpty());
    Creature closestCreature = fCreatures.get(0);
    for (Creature creature : fCreatures)
    {
      int pathSize = (fPathFind.findPath(creature.getLocation(), fGoal)).size();
      if (pathSize < lowestPathSize)
      {
        closestCreature = creature;
        lowestPathSize = pathSize;
      }
    }
    return closestCreature;
  }

  private CreatureList getFactionCreatures()
  {
    CreatureList creatures = new CreatureList();
    creatures.clear();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
      {
        PatientPathFindBehaviour behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
        behaviour.setGrid(fGrid);
        creatures.addElement(creature);
      }
    return creatures;
  }

  private Point2D newGoal()
  {
    Point2D goal_pt = null;

    if (goal_pt == null)
      goal_pt = treasureLocation();

    if (goal_pt  == null)
      goal_pt = heroLocation();

    if (goal_pt  == null)
      goal_pt  = randomLocation();

    return goal_pt;
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

