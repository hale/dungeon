package dungeon.ai.hale;

import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.collections.CreatureList;
import dungeon.ai.Behaviour;

import java.util.Collections;
import java.util.Comparator;

public class FactionBehaviour implements Behaviour {

  Faction faction;
  CreatureList factionCreatures;
  Creature leader;

  public FactionBehaviour(Faction faction)
  {
    this.faction = faction;
    factionCreatures = new CreatureList();
  }

  @Override
  public boolean onTick(Game game)
  {
    Creature rightfulLeader = slowestMember(game);
    if (leader != rightfulLeader)
    {
      leader = rightfulLeader;
      followLeader(leader);
      return true;
    }
    return false;
  }

  private Creature slowestMember(Game game)
  {
    updateFactionCreatures(game);
    Collections.sort(factionCreatures,
      new Comparator<Creature>() {
        public int compare(Creature a, Creature b) {
          return (int) a.getSpeed() - (int) b.getSpeed();
        }
      }
    );
    return factionCreatures.get(0);
  }

  private void updateFactionCreatures(Game game)
  {
    factionCreatures = new CreatureList();
    for (Creature creature : game.getCreatures())
      if (creature.getFaction().equals(faction.getName()))
          factionCreatures.addElement(creature);
  }

  private void followLeader(Creature leader)
  {
    for (Creature follower : factionCreatures)
      follower.setBehaviour(new FollowerBehaviour(follower, leader));
    leader.setBehaviour(new LeaderBehaviour(leader));
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

