package pipe.models;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Arc;
import pipe.models.component.NormalArc;
import pipe.models.component.Token;
import pipe.models.component.Connectable;
import pipe.models.interfaces.IObserver;
import utils.TokenUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArcTest {
    Connectable mockSource;
    Connectable mockTarget;
    IObserver mockObserver;
    Arc arc;

    @Before
    public void setUp()
    {
        mockSource = mock(Connectable.class);
        mockTarget = mock(Connectable.class);
        mockObserver = mock(IObserver.class);
        arc = new NormalArc(mockSource, mockTarget, new HashMap<Token, String>());
    }

    @Test
    public void constructorSetsConnectableInboundOutbound() {
        arc = new NormalArc(mockSource, mockTarget, new HashMap<Token, String>());
        verify(mockSource).addOutbound(arc);
        verify(mockTarget).addInbound(arc);
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

    @Test
    public void gettingStartUsesSourceMathematicsCalculation()
    {
        double angle = setUpSourceXAndYAndReturnAngle();


        Point2D.Double expectedSourcePoint = new Point2D.Double(100, 100);
        when(mockSource.getArcEdgePoint(angle)).thenReturn(expectedSourcePoint);

        Point2D.Double arcSourcePoint = arc.getStartPoint();
        assertEquals(expectedSourcePoint, arcSourcePoint);
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
    public void changingSourceChangesOutbound() {
        Connectable newSource = mock(Connectable.class);
        arc.setSource(newSource);
        verify(mockSource).removeOutboundArc(arc);
        verify(newSource).addOutbound(arc);
    }

    @Test
    public void changingTargetChangesInbound() {
        Connectable newTarget = mock(Connectable.class);
        arc.setTarget(newTarget);
        verify(mockTarget).removeInboundArc(arc);
        verify(newTarget).addInbound(arc);
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
}
