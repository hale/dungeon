// Dumb sample faction class
// YOU SHOULD COMPLETELY CHANGE THIS!

package dungeon.ai.collinson;

import java.util.Random;
import java.awt.geom.Point2D;
import dungeon.model.Game;
import dungeon.model.items.mobs.Faction;
import dungeon.model.items.mobs.Creature;
import dungeon.collections.CreatureList;
import dungeon.ai.Behaviour;
import dungeon.ai.LineOfSight;
import dungeon.ai.CollisionDetection;

public class STFBehaviour implements Behaviour {

	Faction fFaction; // the faction that this behaviour is for
        CreatureList facCreatures; // list of creatures in this faction
        Creature leader; // the leader of this faction
        Creature oldLeader; // Previous leader of faction;
        Random fRandom; // a random number gen. for this faction
        Behaviour leaderBehaviour; // a behaviour object for the leader

	// constructor for behahaviour of faction
	public STFBehaviour(Faction faction)
	{
		fFaction = faction;
                fRandom = new Random();
	}

        // This faction is designed to be pretty dumb
	@Override
	public boolean onTick(Game game) {

           boolean activity = false;

           //get all creatures now in my faction
           facCreatures = creaturesInFaction(game);

           // find leader
           leader = findLeader(game);

           // Set behaviour for leader if new
           if (!leader.equals(oldLeader)){
              leader.setBehaviour(new LeaderBehaviour(leader));
              // record in separate variable for possible use at next tick
              oldLeader = leader;
              activity = true;
           }

           // Get colleagues of leader to try to be close
           for (Creature creat: facCreatures){
              if (!creat.equals(leader)){
                 // change creature behaviour to follower if previously leader
                 if (!(creat.getBehaviour() instanceof FollowerBehaviour)){
                     creat.setBehaviour(new FollowerBehaviour(creat));
                 }
                 // if no current goal then st goal to be close to leader
                 if (creat.getGoal() == null){
                      creat.setGoal(getClosePt(game, creat, 100), game);
                      // App.log(fFaction.getName() + creat.getName() + " following leader");
                 }
              }
           }

           return activity;
        };

        // find an acceptable point close to leader
        // max depth of search = 100 above
        // The LineOfSight method is a bit crude for this purpose.
        // result can be null.
        Point2D getClosePt(Game game, Creature creat, int n){
            Point2D closePoint;
            double x = leader.getLocation().getX() + fRandom.nextDouble() * 8 * (creat.getSize() + leader.getSize());
            double y = leader.getLocation().getY() + fRandom.nextDouble() * 8 * (creat.getSize() + leader.getSize());
            closePoint = new Point2D.Double(x,y);
            // if x,y is not an acceptable point then try again
            if (n > 0){
                if ( ! (CollisionDetection.canOccupy(game, creat, closePoint)
                    &&
                       LineOfSight.exists(creat.getLocation(), closePoint, game.getMap())
                    &&
                       Reachable.dirtyReachable(game, creat.getShape(), closePoint, creat.getSize(), 0.5)
                    ))
                {
                   closePoint = getClosePt(game, creat, n-1);
                }
            }
            else { closePoint = null;}

            return closePoint;
        }

        // Identify a leader for faction
        // Taken to be the first in facCreatures list
        // Might be better to use one of the fastest creatures
        // Note: in tournament, always non-empty on tick
        // since finishes when one fac. becomes empty
        private Creature findLeader(Game game){
              Creature lead = facCreatures.firstElement();
              // App.log("Leader of " + fFaction.getName() + " is " + lead);
              return lead;
        }

        // Return list of creatures in this faction
        private CreatureList creaturesInFaction(Game game){
            CreatureList facCreats = new CreatureList();

            for (Creature creat : game.getCreatures()){
               if (fFaction.getName().equals(creat.getFaction())){
                  facCreats.addElement(creat);
               }
            }
            return facCreats;
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

