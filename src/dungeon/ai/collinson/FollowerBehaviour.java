// Sample behaviour for `follower' creatures
// My creatures are dumb by design
// YOU MUST WRITE YOUR OWN BEHAVIOUR CLASSES!

package dungeon.ai.collinson;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

// import dungeon.App;
import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.actions.ActionPickUp;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.ai.Behaviour;
import dungeon.ai.CollisionDetection;

// Class providing behaviour for `follower' Creature mobs
public class FollowerBehaviour implements Behaviour {

        Creature fCreature = null;
	Random fRandom = new Random();

	/**
	 * Constructor
	 *
	 * @param creature The creature
	 */
	public FollowerBehaviour(Creature creature)
	{
		fCreature = creature;
	}


	// This specifies whether the creature will stay in the room it starts in
	static final boolean KEEP_TO_ROOMS = false;

	// what a creature with this behaviour does on each tick
        // Basically the same as a behaviour seen in an early lab
        // YOU SHOULD HAVE YOUR OWN BEHAVIOUR
	public boolean onTick(Game game)
	{
		// default routine to move the character on a clock tick.

		// first check if the creature can attack something,
		// pick up something, or open a door
		if (ActionAttack.performAction(fCreature, game))
			return true;

		if (ActionPickUp.performAction(fCreature, game))
			return true;

		if (ActionDoor.performAction(fCreature, game))
			return true;

		// if the creature can't do any of the above, figure out how it will move
		if (move(game))
			return true;

		return false;
	}

        // randomised movement that goes with onTick
        // however now picks up goal set by faction onTick
	boolean move(Game game)
	{
		// creature has not moved
		boolean moved = false;

                // move away from other creatures randomly if too close
                moved = repulsion(game);

                // if existing goal is not reachable, ditch that goal
                if (fCreature.getGoal() != null &&
                      ! Reachable.dirtyReachable(game, fCreature.getShape(), fCreature.getGoal(), fCreature.getSize(), 0.5))
                {
                   fCreature.setGoal(null, game);
                }

                // if the creature has a reachable goal move towards it
                // may also be set by faction
		if ((! moved) && fCreature.getGoal() != null)
		{
			moved = fCreature.moveToGoal(game);
		}

		// does the creature have a goal?
                // If not, set one
		if ((! moved) && fCreature.getGoal() == null)
		{
                         // where to look for new goal
			Rectangle2D bounds = game.getMap().getTileAt(fCreature.getLocation()).getArea();

			// pick random goal point within same tile
			double x = bounds.getX() + (bounds.getWidth() * fRandom.nextDouble());
			double y = bounds.getY() + (bounds.getHeight() * fRandom.nextDouble());
			Point2D goal_pt = new Point2D.Double(x, y);

			// check that this point is within a room. not occupied by another creature, etc
			if (CollisionDetection.canOccupy(game, fCreature, goal_pt))
				// all conditions passed, set a new goal
				fCreature.setGoal(goal_pt, game);
		}


		// if the creature hasn't moved towards a goal, make it move randomly
                    if (!moved){
			double theta = fRandom.nextDouble() * Math.PI * 2;
			moved = fCreature.move(theta, game);
                    }

		return moved;
	}


        // movement when repulsed by creature in same faction
        // better version would use potential energy defined across members of faction
        private boolean repulsion(Game game){
           boolean result = false;
           Point2D fLoc = fCreature.getLocation();
           Point2D cLoc = null;
           double personalSpace = 5;

           for (Creature creature : game.getCreatures()){
               cLoc = creature.getLocation();

               if (creature.getFaction().equals(fCreature.getFaction()) && !creature.equals(fCreature)){
                  if (cLoc.distance(fLoc) <  fCreature.getSize() + creature.getSize() + personalSpace){
                     // App.log(fCreature.getFaction() + "creatures too close");
                     // move at random angle, but away from first colleague
                     double psi = Math.asin((cLoc.getY() - fLoc.getY()) / cLoc.distance(fLoc));
                     double theta = psi + Math.PI/2 + fRandom.nextDouble() * Math.PI;
                     fCreature.setGoal(null, game); // clear the old goal
                     result = fCreature.move(theta, game);
                     break;
                  }
               }
           }

           return result;
        }



	public boolean deathTick(Game game) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean gameOverTick(Game game) {
		// TODO Auto-generated method stub
		return false;
	}
}


