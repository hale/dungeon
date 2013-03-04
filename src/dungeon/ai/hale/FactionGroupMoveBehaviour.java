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

  Faction faction;
  CreatureList factionCreatures;
  Creature leader;
  Game fGame;
  Random fRandom = new Random();

  public FactionGroupMoveBehaviour(Faction faction)
  {
    this.faction = faction;
    factionCreatures = new CreatureList();
  }

  @Override
  public boolean onTick(Game game)
  {
    if (fGame == null) fGame = game;

    updateFactionCreatures();
    Point2D goal = getGoalPoint();
    groupMove(goal);

    return true;
  }

  private void groupMove(Point2D goal)
  {
    for (Creature creature : factionCreatures)
    {
      PatientPathFindBehaviour behaviour = (PatientPathFindBehaviour) creature.getBehaviour();
      behaviour.setGoal(goal);
    }
  }

  private void updateFactionCreatures()
  {
    factionCreatures = new CreatureList();
    for (Creature creature : fGame.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
          factionCreatures.addElement(creature);
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

