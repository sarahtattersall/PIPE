package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.models.component.Place;
import pipe.models.component.Token;
import utils.TokenUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlaceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    Place place;

    @Before
    public void setUp() {
        place = new Place("test", "test");
    }

    ;

    @Test
    public void placeObjectIsSelectable() {
        assertTrue(place.isSelectable());
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyAbove() {
        double x1 = 10;
        double y1 = 100;
        double x2 = 10;
        double y2 = 50;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1 + place.getWidth() / 2, y1);
        assertEquals(expected, point);
    }

    private double getAngleBetweenObjects(double x1, double y1, double x2, double y2) {
        double deltax = x1 - x2;
        double deltay = y1 - y2;
        return Math.atan2(deltax, deltay);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyRight() {
        double x1 = 100;
        double y1 = 100;
        double x2 = 200;
        double y2 = 100;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1 + place.getWidth(), y1 + place.getHeight() / 2);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsDirectlyLeft() {
        double x1 = 100;
        double y1 = 100;
        double x2 = 0;
        double y2 = 100;
        double angle = getAngleBetweenObjects(x1, y1, x2, y2);

        place.setX(x1);
        place.setY(y1);

        Point2D.Double point = place.getArcEdgePoint(angle);
        Point2D.Double expected = new Point2D.Double(x1, y1 + place.getHeight() / 2);
        assertEquals(expected, point);
    }

    /**
     * I.e. A->B we're calculating B
     */
    @Test
    public void calculatesCorrectArcAttachmentPointsAsSource() {
        Point2D.Double source = new Point2D.Double(0, 0);
        Point2D.Double target = new Point2D.Double(30, 30);
        double angle = getAngleBetweenObjects(source.x, source.y, target.x, target.y);

        place.setX(source.x);
        place.setY(source.y);

        double FOURTY_FIVE_RADIANS = Math.toRadians(45);
        Point2D.Double expected = new Point2D.Double(Math.sin(FOURTY_FIVE_RADIANS) * place.getWidth() / 2 + 15,
                Math.cos(FOURTY_FIVE_RADIANS) * place.getWidth() / 2 + 15);

        Point2D.Double point = place.getArcEdgePoint(angle);
        assertEquals(expected, point);
    }

    @Test
    public void calculatesCorrectArcAttachmentPointsAsTarget() {
        Point2D.Double source = new Point2D.Double(0, 0);
        Point2D.Double target = new Point2D.Double(30, 30);
        double angle = getAngleBetweenObjects(source.x, source.y, target.x, target.y);

        place.setX(target.x);
        place.setY(target.y);

        double FOURTY_FIVE_RADIANS = Math.toRadians(45);
        Point2D.Double expected = new Point2D.Double(45 - Math.sin(FOURTY_FIVE_RADIANS) * place.getWidth() / 2,
                45 - Math.cos(FOURTY_FIVE_RADIANS) * place.getWidth() / 2);

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

    @Test
    public void decrementExistingTokenDecreasesCount() {
        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.incrementTokenCount(token);

        place.decrementTokenCount(token);
        assertEquals(0, place.getTokenCount(token));
    }

    @Test
    public void tokenCountIsZeroIfPlaceDoesNotContainToken() {
        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        assertEquals(0, place.getTokenCount(token));
    }

    @Test
    public void throwsErrorIfSetTokenCountGreaterThanCapacity() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Cannot set token count that exceeds the capacity");
        place.setCapacity(1);
        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.setTokenCount(token, 2);
    }

    @Test
    public void throwsErrorIfIncrementTokenCountGreaterThanCapacity() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Cannot set token count that exceeds the capacity");
        place.setCapacity(1);

        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.incrementTokenCount(token);
        place.incrementTokenCount(token);
    }

    @Test
    public void capacityZeroMeansNoRestriction() {
        int capacity = 0;
        place.setCapacity(capacity);

        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.incrementTokenCount(token);
    }

    @Test
    public void setTokenCountsCannotExceedCapacity() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Count of tokens exceeds capacity!");
        place.setCapacity(1);

        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();
        tokenCounts.put(token, 10);

        place.setTokenCounts(tokenCounts);
    }

    @Test
    public void changingNumberOfTokensDoesNotTriggerExceedCapacityError() {
        int capacity = 1;
        place.setCapacity(capacity);

        Token token = new Token("red", false, 0, new Color(255, 0, 0));
        place.incrementTokenCount(token);

        place.setTokenCount(token, 1);
        ;
    }

    @Test
    public void correctlyCountsNumberOfTokensStored() {
        int capacity = 20;
        place.setCapacity(capacity);

        int redTokenCount = 3;
        Token redToken = new Token("red", false, 0, new Color(255, 0, 0));
        place.setTokenCount(redToken, 3);

        int blueTokenCount = 10;
        Token blueToken = new Token("red", false, 0, new Color(0, 0, 255));
        place.setTokenCount(blueToken, blueTokenCount);

        assertEquals(redTokenCount + blueTokenCount, place.getNumberOfTokensStored());
    }

    @Test
    public void notifiesObserverOnTokenChange() {
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        Token defaultToken = TokenUtils.createDefaultToken();
        place.addPropertyChangeListener(mockListener);

        place.setTokenCount(defaultToken, 7);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void notifiesObserverOnTokenMapChange() {
        Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        Token defaultToken = TokenUtils.createDefaultToken();
        tokenCounts.put(defaultToken, 7);
        place.addPropertyChangeListener(mockListener);

        place.setTokenCounts(tokenCounts);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }


}
