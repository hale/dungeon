package dungeon.ai.hale;

import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.collections.CreatureList;

import dungeon.ai.Behaviour;

public class FactionBehaviour implements Behaviour {

  Faction fFaction;
  CreatureList factionCreatures;

  public FactionBehaviour(Faction faction)
  {
    fFaction = faction;
  }

  @Override
    public boolean onTick(Game game)
    {
     return false;
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

