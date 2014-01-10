package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.component.Token;
import pipe.models.component.Connectable;
import pipe.models.interfaces.IObserver;
import pipe.models.strategy.arc.ArcStrategy;
import utils.TokenUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArcTest {
    Connectable mockSource;
    Connectable mockTarget;
    IObserver mockObserver;
    Arc arc;
    ArcStrategy mockStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp()
    {
        mockSource = mock(Connectable.class);
        when(mockSource.getId()).thenReturn("source");
        mockTarget = mock(Connectable.class);
        when(mockTarget.getId()).thenReturn("target");
        mockObserver = mock(IObserver.class);
        mockStrategy = mock(ArcStrategy.class);
        arc = new Arc(mockSource, mockTarget, new HashMap<Token, String>(), mockStrategy);
    }

    @Test
    public void notifiesObserversAfterSettingSource()
    {
        arc.registerObserver(mockObserver);
        arc.setSource(mockSource);
        verify(mockObserver).update();
    }

    @Test
    public void notifiesObserversAfterSettingTarget()
    {
        arc.registerObserver(mockObserver);
        arc.setTarget(mockTarget);
        verify(mockObserver).update();
    }

    @Test
    public void notifiesObserversAfterSettingId()
    {
        arc.registerObserver(mockObserver);
        arc.setId("id");
        verify(mockObserver).update();
    }

    @Test
    public void notifiesObserversAfterSettingTagged()
    {
        arc.registerObserver(mockObserver);
        arc.setTagged(false);
        verify(mockObserver).update();
    }

    private double setUpSourceXAndYAndReturnAngle()
    {
        double sourceX = 0;
        double sourceY = 0;
        double targetX = 50;
        double targetY = 100;

        when(mockSource.getX()).thenReturn(sourceX);
        when(mockSource.getY()).thenReturn(sourceY);

        when(mockTarget.getX()).thenReturn(targetX);
        when(mockTarget.getY()).thenReturn(targetY);

        double angle = Math.atan2(sourceX - targetX, sourceY - targetY);
        return angle;
    }

//    @Test
//    public void gettingStartUsesSourceMathematicsCalculation()
//    {
//        double angle = setUpSourceXAndYAndReturnAngle();
//
//
//        Point2D.Double expectedSourcePoint = new Point2D.Double(100, 100);
//        when(mockSource.getArcEdgePoint(angle)).thenReturn(expectedSourcePoint);
//
//        Point2D.Double arcSourcePoint = arc.getStartPoint();
//        assertEquals(expectedSourcePoint, arcSourcePoint);
//    }

    @Test
    public void gettingStartReturnsCenter()
    {
        Point2D.Double center = new Point2D.Double(100, 401);

        when(mockSource.getCentre()).thenReturn(center);

        Point2D.Double arcSourcePoint = arc.getStartPoint();
        assertEquals(center, arcSourcePoint);
    }

    @Test
    public void gettingEndUsesTargetMathematicsCalculation()
    {
        double angle = setUpSourceXAndYAndReturnAngle();


        Point2D.Double expectedEndPoint = new Point2D.Double(100, 100);
        when(mockTarget.getArcEdgePoint(Math.PI + angle)).thenReturn(expectedEndPoint);

        Point2D.Double arcEndPoint = arc.getEndPoint();
        assertEquals(expectedEndPoint, arcEndPoint);
    }

    @Test
    public void returnsTokenWeightForToken() {
        Token defaultToken = TokenUtils.createDefaultToken();
        String weight = "cap(P0)";

        arc.setWeight(defaultToken, weight);
        String actualWeight = arc.getWeightForToken(defaultToken);
        assertEquals(weight, actualWeight);
    }

    @Test
    public void returnsZeroWeightForNonExistantToken() {
        Token defaultToken = TokenUtils.createDefaultToken();
        String actualWeight = arc.getWeightForToken(defaultToken);
        assertEquals("0", actualWeight);
    }

    @Test
    public void returnTrueIfHasFunctionalWeight() {
        Token defaultToken = TokenUtils.createDefaultToken();
        Token redToken = new Token("Default", true, 0, new Color(255, 0, 0));

        arc.setWeight(defaultToken, "2");
        arc.setWeight(redToken, "cap(P0)");

        assertTrue(arc.hasFunctionalWeight());
    }

    @Test
    public void returnFalseIfNoFunctionalWeight() {
        Token defaultToken = TokenUtils.createDefaultToken();
        Token redToken = new Token("Red", true, 0, new Color(255, 0, 0));

        arc.setWeight(defaultToken, "2");
        arc.setWeight(redToken, "4");

        assertFalse(arc.hasFunctionalWeight());
    }

    @Test
    public void settingWeightNotifiesObservers() {
        arc.registerObserver(mockObserver);

        Token defaultToken = TokenUtils.createDefaultToken();
        arc.setWeight(defaultToken, "5");
        verify(mockObserver).update();
    }

    @Test
    public void createsId() {
        assertEquals("source TO target", arc.getId());
    }

    @Test
    public void arcRegistersAsPointObserver() {
        ArcPoint mockPoint = mock(ArcPoint.class);
        arc.addIntermediatePoint(mockPoint);
        verify(mockPoint).registerObserver(arc);
    }

    @Test
    public void arcDeregistersAsPointObserver() {
        ArcPoint mockPoint = mock(ArcPoint.class);
        arc.addIntermediatePoint(mockPoint);
        arc.removeIntermediatePoint(mockPoint);
        verify(mockPoint).removeObserver(arc);
    }


    @Test
    public void registeringPointNotifiesObservers() {
        ArcPoint mockPoint = mock(ArcPoint.class);
        arc.registerObserver(mockObserver);
        arc.addIntermediatePoint(mockPoint);
        verify(mockObserver).update();
    }

    @Test
    public void removingPointNotifiesObservers() {
        ArcPoint mockPoint = mock(ArcPoint.class);
        arc.addIntermediatePoint(mockPoint);
        arc.registerObserver(mockObserver);
        arc.removeIntermediatePoint(mockPoint);
        verify(mockObserver).update();
    }


    @Test
    public void arcObservesArcPoints() {
        ArcPoint point = new ArcPoint(new Point2D.Double(0,0), false);
        arc.addIntermediatePoint(point);
        arc.registerObserver(mockObserver);
        point.notifyObservers();
        verify(mockObserver).update();
    }

    @Test
    public void sourceReturnsTargetAsNextIfNoIntermediatePoints() {
        Point2D.Double center = mock(Point2D.Double.class);
        when(mockSource.getCentre()).thenReturn(center);
        when(mockSource.getX()).thenReturn(0.);
        when(mockSource.getY()).thenReturn(0.);
        when(mockTarget.getX()).thenReturn(0.);
        when(mockTarget.getY()).thenReturn(0.);

        Point2D.Double targetEnd = mock(Point2D.Double.class);
        when(mockTarget.getArcEdgePoint(anyDouble())).thenReturn(targetEnd);

        ArcPoint point = new ArcPoint(center, false);
        ArcPoint actualPoint = arc.getNextPoint(point);
        ArcPoint expectedPoint = new ArcPoint(targetEnd, false);
        assertEquals(expectedPoint, actualPoint);
    }


    @Test
    public void sourceReturnsFirstIntermediatePoint() {
        Point2D.Double center = mock(Point2D.Double.class);
        when(mockSource.getCentre()).thenReturn(center);

        ArcPoint point = new ArcPoint(center, false);
        ArcPoint intermediate = new ArcPoint(new Point2D.Double(1, 5), false);
        arc.addIntermediatePoint(intermediate);
        ArcPoint actualPoint = arc.getNextPoint(point);
        assertEquals(intermediate, actualPoint);
    }

    @Test
    public void intermediateReturnsNextIntermediatePoint() {
        Point2D.Double center = mock(Point2D.Double.class);
        when(mockSource.getCentre()).thenReturn(center);

        ArcPoint intermediate = new ArcPoint(new Point2D.Double(1, 5), false);
        ArcPoint intermediate2 = new ArcPoint(new Point2D.Double(5, 6), true);
        arc.addIntermediatePoint(intermediate);
        arc.addIntermediatePoint(intermediate2);
        ArcPoint actualPoint = arc.getNextPoint(intermediate);
        assertEquals(intermediate2, actualPoint);
    }

    @Test
    public void lastIntermediateReturnsTarget() {
        Point2D.Double targetEnd = mock(Point2D.Double.class);
        when(mockTarget.getArcEdgePoint(anyDouble())).thenReturn(targetEnd);

        ArcPoint intermediate = new ArcPoint(new Point2D.Double(1,1), false);
        arc.addIntermediatePoint(intermediate);
        ArcPoint actualPoint = arc.getNextPoint(intermediate);
        ArcPoint expectedPoint = new ArcPoint(targetEnd, false);
        assertEquals(expectedPoint, actualPoint);
    }

    @Test
    public void throwsExceptionIfNoNextPoint() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("No next point");
        ArcPoint point = new ArcPoint(new Point2D.Double(0,0), false);
        arc.getNextPoint(point);
    }

}
