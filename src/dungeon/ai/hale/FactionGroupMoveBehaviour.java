package dungeon.ai.hale;

import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.collections.CreatureList;
import dungeon.ai.Behaviour;
import dungeon.ai.hale.pathfind.SimplePathFind;
import dungeon.ai.hale.pathfind.Grid;
import dungeon.ai.hale.pathfind.Square;

import java.util.Collections;
import java.util.Comparator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;


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
    if (fGame == null)
    {
      fGame = game;
      fGrid = new Grid(fGame);
      fPathFind = new SimplePathFind(fGame, fGrid);
    }

    fGrid.updateGrid(fGame);

    updateFactionCreatures();

    if (fGoal == null || goalReached())
    {
      // set new goal
      fGoal = newGoal();
      // todo: set the leader for this goal
    }

    // can we do this
    if (fGoal != null)
      setCreatureGoals();


    return true;
  }

  private void setLeader()
  {

  }

  private void setCreatureGoals()
  {
    Creature leader = closestCreatureToGoal();
    PatientPathFindBehaviour behaviour;
    for (Creature creature : fCreatures)
    {
      //if (creature.getGoal() !=null) continue;
      behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
      if (creature.equals(leader))
        behaviour.setDest(fGoal);
      else
        behaviour.setDest(leader.getLocation());
    }
  }

  private Creature closestCreatureToGoal()
  {
    System.out.println("finding closest creature");
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

  private void updateFactionCreatures()
  {
    fCreatures.clear();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
      {
        PatientPathFindBehaviour behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
        behaviour.setGrid(fGrid);
        behaviour.setPathFind(fPathFind);
        fCreatures.addElement(creature);
      }
  }

  private Point2D newGoal()
  {
    Point2D goal_pt = null;
    if (goal_pt == null)
      goal_pt = treasureLocation();
    //if (goal_pt  == null)
      //goal_pt = heroLocation();
    //if (goal_pt  == null)
      //goal_pt  = randomLocation();
    return goal_pt;
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

