// Behaviour for Leader creature
// My creatures are dumb by design.
// YOU MUST WRITE YOUR OWN BEHAVIOUR CLASSES!

package dungeon.ai.collinson;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
// import dungeon.App;
import dungeon.model.Game;
import dungeon.model.items.mobs.Creature;
import dungeon.model.items.treasure.Treasure;
import dungeon.model.structure.Tile;
import dungeon.model.structure.Map;
import dungeon.collections.TileList;
import dungeon.ai.actions.ActionAttack;
import dungeon.ai.actions.ActionPickUp;
import dungeon.ai.actions.ActionDoor;
import dungeon.ai.Behaviour;
import dungeon.ai.CollisionDetection;
import dungeon.ai.LineOfSight;

import java.util.Random;

public class LeaderBehaviour implements Behaviour {

	Creature fCreature = null;
        String fFaction;
	List<Tile> roomsCleared;
	Queue<Point2D> goalQueue;
        Random fRandom;

        // Leader constructor
	public LeaderBehaviour(Creature creature)
	{
		fCreature = creature;
                fFaction = fCreature.getFaction();
		roomsCleared = new ArrayList<Tile>();
		goalQueue = new ArrayDeque<Point2D>();
                fRandom = new Random();
	}


        // onTick method adapted from an early lab
        // move method has changed a bit
	@Override
	public boolean onTick(Game game) {

                if (ActionAttack.performAction(fCreature, game))
			return true;

		// pick up treasure if possible
		if (ActionPickUp.performAction(fCreature, game)) {
			return true;
		}

                if (ActionDoor.performAction(fCreature, game))
			return true;

		// else move
		if (move(game))
			return true;

		return false;
	}


        // determine how creature should move
	boolean move(Game game)
	{
		// flag for method
		boolean moved = false;

		// case 1: no current goal, goal queue is not empty;
		// Get next goal from queue
                setNewGoal(game);

		// case 2: no current goal, goal queue is empty
                //
		if (fCreature.getGoal() == null && goalQueue.isEmpty())
		{

                    // if there is treasure in my tile and I can carry it,
                    //    then set it to be my goal
                      treasureGoal(game);

                    // if goal queue is still empty
		    // set goals to move to an adjacent tile
                    // first randomize order of tile in tile list
                    if (goalQueue.isEmpty()){ changeTile(game); }
                }

		// if goal exists then move to it
                moveCritter(game, moved);

                // return flag
		return moved;
	}

       // method for setting goal seq. to change tile
       private void changeTile(Game game){
                    Map map = game.getMap();
	            Tile myTile = map.getTileAt(fCreature.getLocation());

			for (Tile otherTile: randomPerm(map.getTiles())) {
			  if (!myTile.equals(otherTile) && myTile.touches(otherTile)) {
			    // found suitable candidate.  In order to move into it move through five points
                            // Suupose the tile is at least as wide as it is long,
                            // (1) Same x, old tile center y
                            // (2) Touch point (between old and new tiles) x, old tile center y
                            // (3) touch point
                            // if new is wider than it is high
                            // (4)  touch point x, new tile center y
                            // (5)  new tile center x, new tile center y
			    // alternatives if either is higher than wide
                              Point2D firstPoint;
                              Point2D secondPoint;
                              Point2D fourthPoint;
                              Point2D touchPoint = myTile.getTouchPoint(otherTile);

                              if (myTile.getArea().getWidth() >= myTile.getArea().getHeight()){
                                firstPoint = new Point2D.Double(fCreature.getLocation().getX(), myTile.getArea().getCenterY());
                                secondPoint = new Point2D.Double(touchPoint.getX(), firstPoint.getY());
                              }
                              else {
                                firstPoint = new Point2D.Double(myTile.getArea().getCenterX(), fCreature.getLocation().getY());
                                secondPoint = new Point2D.Double(firstPoint.getX(), touchPoint.getY());
                              }

                              if (otherTile.getArea().getWidth() >= otherTile.getArea().getHeight()){
                                fourthPoint = new Point2D.Double(touchPoint.getX(), otherTile.getArea().getCenterY());
                              }
                              else {
                                fourthPoint = new Point2D.Double(otherTile.getArea().getCenterY(), touchPoint.getY());
                              }

                              goalQueue.clear(); // first, clear goal queue
                              goalQueue.offer(firstPoint);
                              goalQueue.offer(secondPoint);
                              goalQueue.offer(touchPoint);
                               //App.log(touchPoint + "added to goal queue");
                              goalQueue.offer(fourthPoint);
                              Point2D newCentre = new Point2D.Double(otherTile.getArea().getCenterX(), otherTile.getArea().getCenterY());
			      goalQueue.offer(newCentre);
                              //App.log(newCentre + "added to goal queue");

                              break; // important to just take the first `other tile'
		           }
		        }
        }

        // move creature to goal, if it exists
        void moveCritter(Game game, boolean moved){
            if (fCreature.getGoal() != null){

               // check goal is ok: if not, set random goal
	       if (! goalCheck(game)){
                // App.log(fFaction + fCreature + " collision detection for " + fCreature.getGoal());
		       fCreature.setGoal(randomGoal(game, fCreature.getLocation()), game);
               }

		    moved = fCreature.moveToGoal(game);
                    // App.log(fFaction + fCreature + " is going to goal at " + fCreature.getGoal());
		}
        }

        // check goal doesn't cause a problem
        // if collison problem set a random goal
        // plus, a quick and dirty hack to stop creature getting stuck on edges (naughty)
        boolean goalCheck(Game game){
            Point2D myGoal = fCreature.getGoal();
            double mySize = fCreature.getSize();
            Rectangle2D myShape = fCreature.getShape();
            double margin = 0.5;

            return (CollisionDetection.canOccupy(game, fCreature, fCreature.getGoal())
                        &&
                            LineOfSight.exists(fCreature.getLocation(), fCreature.getGoal(), game.getMap())
                        &&
                           Reachable.dirtyReachable(game, myShape, myGoal, mySize, margin)
                        );
        }


        //check a point on a wall to see if it is a possible contact point between tiles
        boolean isaContact(Game game, Tile tile, double x, double y){
          Point2D pt = new Point2D.Double(x,y);
          boolean touched = false;

          for (Tile tl : game.getMap().getTiles()){
             if (! tl.equals(tile)){
                 if (tl.getArea().getMinX() <= x &&
                     x <= tl.getArea().getMaxX() &&
                     tl.getArea().getMinY() <= y &&
                     y <= tl.getArea().getMaxY()) {
                     touched = true;
                     break;
                 }
             }
          }

          return touched;
        }

        // find a random goal on center line of long axis of the same tile as given point
        // keep looping until findable one (but quit after 50 iterations)
        Point2D randomGoal(Game game, Point2D pt){
             Point2D candidatePt = pt;
             Tile fTile = game.getMap().getTileAt(pt);
             Rectangle2D fRect = fTile.getArea();
             double fwidth = fRect.getWidth();
             double fheight = fRect.getHeight();
             int n = 50;
             boolean notFound = true;

             if (fwidth >= fheight){

               while (n > 0 && ! notFound){
                  double x = fRect.getX() + (fwidth * fRandom.nextDouble());
                  candidatePt = new Point2D.Double(x, fRect.getCenterY());

                  if (CollisionDetection.canOccupy(game, fCreature, candidatePt)){
                      notFound = false;
                  }

                  n = n--;
               }

             }
             else {

               while (n > 0 && ! notFound){
                  double y = fRect.getY() + (fheight * fRandom.nextDouble());
                  candidatePt = new Point2D.Double(fRect.getCenterX(), y);

                  if (CollisionDetection.canOccupy(game, fCreature, candidatePt)){
                      notFound = false;
                  }

                  n = n--;
               }
             }

             if (notFound){ candidatePt = pt; }

             return candidatePt;
        }

        // if there is treasure in my tile and I can carry it then set it to be one of my goals
        private void treasureGoal(Game game){
            for (Treasure treas : game.getTreasure()){
                     // get tile of treasure
                     Point2D loc = treas.getLocation();
                     Tile treasTile = game.getMap().getTileAt(loc);
                     // check treasure lies on given tile and offer as goal point
                     if (treasTile == game.getMap().getTileAt(fCreature.getLocation()) &&
                             treas.getWeight() + fCreature.getEncumbrance() <= fCreature.getStrength()){
                        goalQueue.offer(loc);
                     }
                   }
             }

        // set new goal, where no goal, empty goal queue
        void setNewGoal(Game game){
           if (fCreature.getGoal() == null && !goalQueue.isEmpty()) {
                 Point2D newGoal = goalQueue.remove();
                 fCreature.setGoal(newGoal, game);
		 //App.log(fCreature + " is going to goal at " + fCreature.getGoal());
           }
        }

        // generate random premutation of given TileList
        private TileList randomPerm (TileList tList) {
            TileList outList = new TileList();
            int tlength = tList.size();
            List<Integer> intPerm = genPerm(tlength);
            for (int i = 0;i<tlength;i++){
                Tile chosen = tList.elementAt(intPerm.get(i));
                outList.add(i, chosen);
            }
            return outList;
        }

        // generate random permutation of integers 0,...,n-1
        private List<Integer> genPerm(int n){
            //list to be output
            List<Integer> outList = new ArrayList<Integer>();
            // remaining integers to be taken
            List<Integer> remList = new ArrayList<Integer>();
            for (int i=0;i<n;i++){
                remList.add(i,i);
            }
            // remove ints from remList by random sampling and add to outList
            for (int i=0;i<n;i++){
                int index = sample(n-i);
                int chosen = remList.remove(index);
                outList.add(i, chosen);
            }
            // output the result
            return outList;
        }

        // generate a uniform random sample from n integers 0,...n-1
        private int sample (int n){
            // generate uniform sample in [0,1]
            double ran = Math.random();
            // App.log("ran is" + ran);
            int sample = 0;
            boolean notYet = true;
            double ndouble = (double) n;

            for (int i=0; i<n && notYet ; i++){
                if (ran <= i / ndouble){
                    notYet = false;
                    sample = i;
                    // App.log("Sample is " + sample + " in 0,...,"  + (n-1));
                }
            }

            return sample;
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
