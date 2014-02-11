package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import utils.TokenUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArcTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Place mockSource;

    @Mock
    Transition mockTarget;

    Arc<Place, Transition> arc;

    @Before
    public void setUp() {
        when(mockSource.getId()).thenReturn("source");
        when(mockTarget.getId()).thenReturn("target");
        arc = new Arc<>(mockSource, mockTarget, new HashMap<Token, String>(), ArcType.NORMAL);
    }

    @Test
    public void gettingStartReturnsCenter() {
        Point2D.Double center = new Point2D.Double(100, 401);

        when(mockSource.getCentre()).thenReturn(center);

        Point2D.Double arcSourcePoint = arc.getStartPoint();
        assertEquals(center, arcSourcePoint);
    }

    @Test
    public void calculatesCorrectAngleTargetRightOfSource() {
        when(mockSource.getX()).thenReturn(0);
        when(mockSource.getY()).thenReturn(0);
        when(mockTarget.getX()).thenReturn(50);
        when(mockTarget.getY()).thenReturn(0);

        arc.getEndPoint();
        verify(mockTarget).getArcEdgePoint(Math.toRadians(0));
    }


    @Test
    public void calculatesCorrectAngleTargetLeftOfSource() {
        when(mockSource.getX()).thenReturn(50);
        when(mockSource.getY()).thenReturn(0);
        when(mockTarget.getX()).thenReturn(0);
        when(mockTarget.getY()).thenReturn(0);

        arc.getEndPoint();
        verify(mockTarget).getArcEdgePoint(Math.toRadians(180));
    }

    @Test
    public void calculatesCorrectAngleTargetBelowSource() {
        when(mockSource.getX()).thenReturn(0);
        when(mockSource.getY()).thenReturn(0);
        when(mockTarget.getX()).thenReturn(0);
        when(mockTarget.getY()).thenReturn(50);

        arc.getEndPoint();
        verify(mockTarget).getArcEdgePoint(Math.toRadians(90));
    }

    @Test
    public void calculatesCorrectAngleTargetAboveSource() {
        when(mockSource.getX()).thenReturn(0);
        when(mockSource.getY()).thenReturn(50);
        when(mockTarget.getX()).thenReturn(0);
        when(mockTarget.getY()).thenReturn(0);

        arc.getEndPoint();
        verify(mockTarget).getArcEdgePoint(Math.toRadians(-90));
    }

    private double setUpSourceXAndYAndReturnAngle() {
        int sourceX = 0;
        int sourceY = 0;
        int targetX = 50;
        int targetY = 100;

        when(mockSource.getX()).thenReturn(sourceX);
        when(mockSource.getY()).thenReturn(sourceY);

        when(mockTarget.getX()).thenReturn(targetX);
        when(mockTarget.getY()).thenReturn(targetY);

        return Math.atan2(sourceY - targetY, sourceX - targetX);
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
    public void createsId() {
        assertEquals("source TO target", arc.getId());
    }

    @Test
    public void sourceReturnsTargetAsNextIfNoIntermediatePoints() {
        Point2D.Double center = mock(Point2D.Double.class);
        when(mockSource.getCentre()).thenReturn(center);
        when(mockSource.getX()).thenReturn(0);
        when(mockSource.getY()).thenReturn(0);
        when(mockTarget.getX()).thenReturn(0);
        when(mockTarget.getY()).thenReturn(0);

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

        ArcPoint intermediate = new ArcPoint(new Point2D.Double(1, 1), false);
        arc.addIntermediatePoint(intermediate);
        ArcPoint actualPoint = arc.getNextPoint(intermediate);
        ArcPoint expectedPoint = new ArcPoint(targetEnd, false);
        assertEquals(expectedPoint, actualPoint);
    }

    @Test
    public void throwsExceptionIfNoNextPoint() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("No next point");
        ArcPoint point = new ArcPoint(new Point2D.Double(20, 15), false);
        arc.getNextPoint(point);
    }

}
