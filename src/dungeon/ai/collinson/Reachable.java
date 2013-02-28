

package dungeon.ai.collinson;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import dungeon.model.Game;
import dungeon.ai.LineOfSight;

public class Reachable {

        // use to check that edges and corners don't obstruct travel of craeture
        // pt1 is centre of creat, pt2 is target point, sz is creature size
        // public static boolean reachable(Game game, Point2D pt1, Point2D pt2, double sz){

        // an ugly heuristic version of the above
        public static boolean dirtyReachable(Game game, Rectangle2D myShape, Point2D myGoal, double mySize, double margin){
          return
             LineOfSight.exists(new Point2D.Double(myShape.getMinX() - margin, myShape.getCenterY()),
                                new Point2D.Double(myGoal.getX() - (mySize + margin), myGoal.getY()),
                                game.getMap())
          &&
             LineOfSight.exists(new Point2D.Double(myShape.getMaxX() + margin, myShape.getCenterY()),
                                new Point2D.Double(myGoal.getX() + (mySize + margin), myGoal.getY()),
                                game.getMap())
          &&
              LineOfSight.exists(new Point2D.Double(myShape.getCenterX(), myShape.getMinY() - margin),
                                 new Point2D.Double(myGoal.getX() , myGoal.getY() - (mySize + margin)),
                                 game.getMap())
          &&
              LineOfSight.exists(new Point2D.Double(myShape.getCenterX(), myShape.getMaxY() + margin),
                                new Point2D.Double(myGoal.getX() , myGoal.getY() + (mySize + margin)),
                                game.getMap());
    }

}

