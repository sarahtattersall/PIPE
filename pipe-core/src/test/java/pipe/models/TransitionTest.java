package pipe.models;

import org.junit.Test;
import pipe.models.component.transition.Transition;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

public class TransitionTest {


    @Test
    public void calculatesCorrectArcConnectionForTransitionAbove() {
        Transition transition = new Transition("id", "name");
        // No rotation
        transition.setAngle(0);

        double sourceX = 0;
        double sourceY = 50;
        double targetX = 0;
        double targetY = 0;
        double angle = getAngleBetweenObjects(sourceX, sourceY, targetX, targetY);

        transition.setX(targetX);
        transition.setY(targetY);

        assertEquals(-90, Math.toDegrees(angle),0.001);
        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(targetX + transition.getWidth()/2, targetY + transition.getHeight());
        assertEquals(expected, point);
    }


    @Test
    public void calculatesCorrectArcConnectionPointForTransitionRight() {
        Transition transition = new Transition("id", "name");
        // No rotation
        transition.setAngle(0);

        double sourceX = 0;
        double sourceY = 0;
        double targetX = 50;
        double targetY = 0;
        double angle = getAngleBetweenObjects(sourceX, sourceY, targetX, targetY);

        transition.setX(targetX);
        transition.setY(targetY);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(targetX,
                targetY + transition.getHeight() / 2);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcConnectionPointForTargetOnLeft() {
        Transition transition = new Transition("id", "name");
        // No rotation
        transition.setAngle(0);

        double sourceX = 50;
        double sourceY = 0;
        double targetX = 0;
        double targetY = 0;
        double angle = getAngleBetweenObjects(sourceX, sourceY, targetX, targetY);

        transition.setX(targetX);
        transition.setY(targetY);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(targetX + transition.getWidth(),
                targetY + transition.getHeight() / 2);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcConnectionPointForBottomRotated180() {
        Transition transition = new Transition("id", "name");
        transition.setAngle(180);

        double x1 = 100;
        double y1 = 100;
        double x2 = 100;
        double y2 = 200;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        transition.setX(x1);
        transition.setY(y1);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(100, 115);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcConnectionPointForTransitionBelowRotated90() {
        Transition transition = new Transition("id", "name");
        transition.setAngle(90);

        double sourceX = 0;
        double sourceY = 0;
        double targetX = 0;
        double targetY = 50;
        double angle = getAngleBetweenObjects(sourceX, sourceY, targetX, targetY);

        transition.setX(targetX);
        transition.setY(targetY);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(5,70);
        assertEquals(expected, point);
    }


    private double getAngleBetweenObjects(double x1, double y1, double x2, double y2) {
        double deltax = x2 - x1;
        double deltay = y2 - y1;
        return Math.atan2(deltay, deltax);
    }
}
