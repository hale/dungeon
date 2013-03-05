package dungeon.ai.hale;

import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.collections.CreatureList;
import dungeon.ai.Behaviour;
import dungeon.ai.hale.pathfind.SimplePathFind;

import java.util.Collections;
import java.util.Comparator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.List;


public class FactionGroupMoveBehaviour implements Behaviour {

  SimplePathFind fPathFind;
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

    this.fCreatures = getFactionCreatures();

    // TODO: only get new goal when the faction goal has been reached
    this.fGoal = newGoal();

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
    for (Creature creature : fCreatures)
    {
      fPathFind = new SimplePathFind(creature, fGame);
      behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
      behaviour.setPath(fPathFind.findPath(creature.getLocation(), fGoal));
    }
    Creature leader = closestCreatureToGoal();
    for (Creature creature : fCreatures)
      if (!creature.equals(leader))
      {
        fPathFind = new SimplePathFind(creature, fGame);
        behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
        behaviour.setPath(fPathFind.findPath(creature.getLocation(), leader.getLocation()));
      }
  }

  private Creature closestCreatureToGoal()
  {
    PatientPathFindBehaviour behaviour;
    Creature closestCreature = fCreatures.get(0);
    PatientPathFindBehaviour closestCreatureBehaviour = (PatientPathFindBehaviour) closestCreature.getBehaviour();
    for (Creature creature : fCreatures)
    {
      behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
      if (behaviour.getPathSize() > closestCreatureBehaviour.getPathSize())
        closestCreature = creature;
    }
    return closestCreature;
  }

  private CreatureList getFactionCreatures()
  {
    CreatureList creatures = new CreatureList();
    creatures.clear();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
        creatures.addElement(creature);
    return creatures;
  }

  private Point2D newGoal()
  {
    Point2D goal_pt = null;;

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

