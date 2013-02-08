package dungeon.ai.actions;

import java.util.Vector;

import dungeon.model.Game;
import dungeon.model.combat.Attack;
import dungeon.model.combat.Longbow;
import dungeon.model.items.mobs.Mob;
import dungeon.utils.State;
import dungeon.utils.Trigger;

/**
 * Helper class to perform an attack
 */
public class ActionAttack
{
  /**
   * Make the given mob perform an attack if possible
   *
   * @param mob The mob
   * @param game The current game object
   * @return Returns true if the mob made an attack; false otherwise
   */
  public static boolean performAction(Mob mob, Game game)
  {
    for (Attack attack: mob.getAttacks())
    {
      if (mob.getCurrentEnergy() < attack.getEnergyCost()) {
        if (mob.isInState(State.Offensive) && attack instanceof Longbow)
          mob.fire(Trigger.InsufficientEnergy);
        continue;
      }
      if (mob.isInState(State.Defensive) && attack instanceof Longbow)
        mob.fire(Trigger.SufficientEnergy);

      Vector<Mob> targets = mob.findTargets(game, attack);
      if (targets.size() == 0)
        continue;

      // Attack a target
      Mob target = targets.get(0);
      attack.useAttack(mob, target);

      return true;
    }

    return false;
  }
}
