package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.views.arc.NormalArcView;
import pipe.views.viewComponents.ArcPath;

@RunWith(MockitoJUnitRunner.class)
public class ArcViewTest {

    private ArcView<Place, Transition> arcView;

    @Mock
    private Arc<Place, Transition> mockArc;
    private Point2D.Double start = new Point2D.Double(50, 39);
    private Point2D.Double end = new Point2D.Double(100, 500);
    private Place source;
    private Transition target;

    @Before
    public void setUp() {
        PetriNetController mockController = mock(PetriNetController.class);
        source = new Place("P0","P0");
        target =  new Transition("T0", "T0");
        when(mockArc.getSource()).thenReturn(source);
        when(mockArc.getTarget()).thenReturn(target);
        when(mockArc.getStartPoint()).thenReturn(start);
        when(mockArc.getEndPoint()).thenReturn(end);
        arcView = new NormalArcView<>(mockArc, mockController);
    }

    @Test
    public void ArcViewSetsCorrectPathInConstructor() {
        ArcPath path = arcView.getArcPath();
        assertEquals(2, path.getNumPoints());
        assertEquals(start, path.getPoint(0));
        assertEquals(end, path.getPoint(1));
    }

    @Test
    public void arcListensToSourceXChanges() {
        Point2D.Double newStartPoint = new Point2D.Double(10,20);
        when(mockArc.getStartPoint()).thenReturn(newStartPoint);

        Point2D.Double newEndPoint = new Point2D.Double(10,500);
        when(mockArc.getEndPoint()).thenReturn(newEndPoint);

        source.setX(newStartPoint.getX());

        assertEquals(newStartPoint, arcView.getArcPath().getPoint(0));
        assertEquals(newEndPoint, arcView.getArcPath().getPoint(arcView.getArcPath().getEndIndex()));
    }

    @Test
    public void arcListensToSourceYChanges() {
        Point2D.Double newStartPoint = new Point2D.Double(10,20);
        when(mockArc.getStartPoint()).thenReturn(newStartPoint);

        Point2D.Double newEndPoint = new Point2D.Double(10,500);
        when(mockArc.getEndPoint()).thenReturn(newEndPoint);

        source.setY(newStartPoint.getY());

        assertEquals(newStartPoint, arcView.getArcPath().getPoint(0));
        assertEquals(newEndPoint, arcView.getArcPath().getPoint(arcView.getArcPath().getEndIndex()));
    }

    @Test
    public void arcListensToTargetXChanges() {
        Point2D.Double newStartPoint = new Point2D.Double(50,20);
        when(mockArc.getStartPoint()).thenReturn(newStartPoint);

        Point2D.Double newEndPoint = new Point2D.Double(10,500);
        when(mockArc.getEndPoint()).thenReturn(newEndPoint);

        target.setY(newEndPoint.getX());

        assertEquals(newStartPoint, arcView.getArcPath().getPoint(0));
        assertEquals(newEndPoint, arcView.getArcPath().getPoint(arcView.getArcPath().getEndIndex()));
    }

    @Test
    public void arcListensToTargetYChanges() {
        Point2D.Double newStartPoint = new Point2D.Double(50,20);
        when(mockArc.getStartPoint()).thenReturn(newStartPoint);

        Point2D.Double newEndPoint = new Point2D.Double(10,500);
        when(mockArc.getEndPoint()).thenReturn(newEndPoint);

        target.setY(newEndPoint.getY());

        assertEquals(newStartPoint, arcView.getArcPath().getPoint(0));
        assertEquals(newEndPoint, arcView.getArcPath().getPoint(arcView.getArcPath().getEndIndex()));
    }
}
