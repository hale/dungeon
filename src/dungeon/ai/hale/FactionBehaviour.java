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


public class FactionBehaviour implements Behaviour {

  Faction faction;
  CreatureList factionCreatures;
  Creature leader;
  Game fGame;
  Random fRandom = new Random();

  public FactionBehaviour(Faction faction)
  {
    this.faction = faction;
    factionCreatures = new CreatureList();
  }

  @Override
  public boolean onTick(Game game)
  {
    if (fGame == null) fGame = game;

    Creature leader = slowestMember();
    followLeader(leader);

    return false;
  }

  private Creature slowestMember()
  {
    updateFactionCreatures();
    Collections.sort(factionCreatures,
      new Comparator<Creature>() {
        public int compare(Creature a, Creature b) {
          return (int) a.getSpeed() - (int) b.getSpeed();
        }
      }
    );
    //System.out.println("LEADER: " + factionCreatures.get(0));
    return factionCreatures.get(0);
  }

  private void updateFactionCreatures()
  {
    factionCreatures = new CreatureList();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
          factionCreatures.addElement(creature);
  }

  private void followLeader(Creature leader)
  {
    Point2D goal = null;
    SimplePathFind pathFinder;
    List<Point2D> path;
    SlaveBehaviour creatureBehaviour;
    for (Creature creature : factionCreatures)
    {
      creatureBehaviour = (SlaveBehaviour) creature.getBehaviour();
      //if (creatureBehaviour.hasPath()) { continue; }

      pathFinder = new SimplePathFind(creature, fGame);
      if (creature.equals(leader))
        path = pathFinder.findPath(creature.getLocation(), getGoalPoint());
      else
      {
        // Maybe instead find path to the leader's previous tile?
        path = pathFinder.findPath(creature.getLocation(), leader.getLocation());
        if (path.size() < 2)
          path = null;
        else
        {
          path.remove(path.size()-1);
          path.remove(path.size()-1);
        }
      }
      if (path != null)
        creatureBehaviour.setPath(path);
    }
  }

  private Point2D getGoalPoint()
  {
    Point2D goal_pt = null;
    if (!fGame.getTreasure().isEmpty())
      goal_pt = fGame.getTreasure().get(0).getLocation();;

    if (goal_pt == null)
      goal_pt = randomLocation();

    goal_pt = fGame.getHero().getLocation();

    assert(goal_pt != null);
    return goal_pt;
  }

  private Point2D randomLocation()
  {
    Rectangle2D bounds = fGame.getMap().getBounds(0);

    double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
    double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
    return new Point2D.Double(x, y);
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

