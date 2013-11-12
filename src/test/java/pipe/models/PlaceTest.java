package pipe.models;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PlaceTest {

    Place place;

    @Before
    public void setUp()
    {
        place = new Place("test", "test");
    };


    @Test
    public void placeObjectIsSelectable()
    {
       assertTrue(place.isSelectable());
    }


    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyAbove()
    {
        double x1 = 10;
        double y1 = 100;
        double x2 = 10;
        double y2 = 50;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(angle);
        Point2D.Double expected  = new Point2D.Double(x1 + place.getWidth()/2, y1);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyRight()
    {
        double x1 = 100;
        double y1 = 100;
        double x2 = 200;
        double y2 = 100;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(angle);
        Point2D.Double expected  = new Point2D.Double(x1 + place.getWidth(), y1 + place.getHeight()/2);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyLeft()
    {
        double x1 = 100;
        double y1 = 100;
        double x2 = 0;
        double y2 = 100;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(angle);
        Point2D.Double expected  = new Point2D.Double(x1, y1 + place.getHeight()/2);
        assertEquals(expected, point);
    }

    /**
     * I.e. A->B we're calculating B
     */
    @Test
    public void calculatesCorrectArcAttachmentPointsAsSource()
    {
        Point2D.Double source = new Point2D.Double(0, 0);
        Point2D.Double target = new Point2D.Double(30, 30);
        double angle = getAngleBetweenObjects(source.x, source.y, target.x, target.y);

        place.setX(source.x);
        place.setY(source.y);

        double FOURTY_FIVE_RADIANS = Math.toRadians(45);
        Point2D.Double expected = new Point2D.Double(Math.sin(FOURTY_FIVE_RADIANS) * place.getWidth()/2 + 15,
                Math.cos(FOURTY_FIVE_RADIANS) * place.getWidth()/2 + 15);

        Point2D.Double point = place.getArcEdgePoint(angle);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsAsTarget()
    {
        Point2D.Double source = new Point2D.Double(0, 0);
        Point2D.Double target = new Point2D.Double(30, 30);
        double angle = getAngleBetweenObjects(source.x, source.y, target.x, target.y);

        place.setX(target.x);
        place.setY(target.y);

        double FOURTY_FIVE_RADIANS = Math.toRadians(45);
        Point2D.Double expected = new Point2D.Double(45 - Math.sin(FOURTY_FIVE_RADIANS) * place.getWidth()/2,
                45 - Math.cos(FOURTY_FIVE_RADIANS) * place.getWidth()/2);

        Point2D.Double point = place.getArcEdgePoint(Math.PI + angle);
        assertEquals(expected, point);
    }

    @Test
    public void addNewTokenSetsCountToOne() {
        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.incrementTokenCount(token);
        assertEquals(1, place.getTokenCount(token));

    }

    @Test
    public void addExistingTokenIncrementsCount() {
        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.incrementTokenCount(token);

        place.incrementTokenCount(token);
        assertEquals(2, place.getTokenCount(token));
    }

    private double getAngleBetweenObjects(double x1, double y1, double x2, double y2)
    {
        double deltax = x1 - x2;
        double deltay = y1 - y2;
        return Math.atan2(deltax, deltay);
    }


}
