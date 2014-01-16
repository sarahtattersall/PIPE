package pipe.models;

import org.junit.Test;
import pipe.models.component.transition.Transition;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

public class TransitionTest {


    @Test
    public void calculatesCorrectArcConnectionPointForAbove() {
        Transition transition = new Transition("id", "name");
        // No rotation
        transition.setAngle(0);

        double x1 = 10;
        double y1 = 100;
        double x2 = 10;
        double y2 = 50;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        transition.setX(x1);
        transition.setY(y1);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1 + transition.getHeight() / 2, y1);
        assertEquals(expected, point);
    }


    @Test
    public void calculatesCorrectArcConnectionPointForRight() {
        Transition transition = new Transition("id", "name");
        // No rotation
        transition.setAngle(0);

        double x1 = 10;
        double y1 = 100;
        double x2 = 50;
        double y2 = 100;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        transition.setX(x1);
        transition.setY(y1);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1 + transition.getHeight() / 2 + transition.getWidth() / 2,
                y1 + transition.getHeight() / 2);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcConnectionPointForLeft() {
        Transition transition = new Transition("id", "name");
        // No rotation
        transition.setAngle(0);

        double x1 = 200;
        double y1 = 100;
        double x2 = 100;
        double y2 = 100;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        transition.setX(x1);
        transition.setY(y1);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1 + transition.getHeight() / 2 - transition.getWidth() / 2,
                y1 + transition.getHeight() / 2);
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
        Point2D.Double expected = new Point2D.Double(x1 + transition.getHeight() / 2, y1 + transition.getHeight());
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcConnectionPointForBottomRotated90() {
        Transition transition = new Transition("id", "name");
        transition.setAngle(90);

        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 50;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        transition.setX(x1);
        transition.setY(y1);

        Point2D.Double point = transition.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1 + transition.getHeight() / 2,
                y1 + transition.getHeight() - transition.getWidth());
        assertEquals(expected, point);
    }


    private double getAngleBetweenObjects(double x1, double y1, double x2, double y2) {
        double deltax = x1 - x2;
        double deltay = y1 - y2;
        return Math.atan2(deltax, deltay);
    }
}
