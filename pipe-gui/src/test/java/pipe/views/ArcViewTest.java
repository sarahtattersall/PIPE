package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import pipe.controllers.PetriNetController;
import pipe.gui.ZoomController;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Transition;
import pipe.views.viewComponents.ArcPath;

public class ArcViewTest {

    private ArcView<Place, Transition> arcView;
    private Arc<Place, Transition> mockArc;
    private Point2D.Double start = new Point2D.Double(50, 39);
    private Point2D.Double end = new Point2D.Double(100, 500);
    private static final double DOUBLE_DELTA = 0.0001;
    private Place source;

    @Before
    public void setUp() throws Exception {
        PetriNetController mockController = mock(PetriNetController.class);
        mockArc = mock(Arc.class);
        source = new Place("","");
        when(mockArc.getSource()).thenReturn(source);
        when(mockArc.getStartPoint()).thenReturn(start);
        when(mockArc.getEndPoint()).thenReturn(end);
        arcView = new NormalArcView<Place, Transition>(mockArc, mockController);
    }

    @Test
    public void ArcViewSetsCorrectPathInConstructor() {
        ArcPath path = arcView.getArcPath();
        assertEquals(2, path.getNumPoints());
        assertEquals(start, path.getPoint(0));
        assertEquals(end, path.getPoint(1));
    }


    @Test
    public void zoomUpdatesPoints() {
        int zoom = 110;
        arcView.zoomUpdate(zoom);
        ArcPath path = arcView.getArcPath();
        assertEquals(2, path.getNumPoints());
        Point2D.Double zoomedStart =  ZoomController.getZoomedValue(start, zoom);
        Point2D.Double actualStart = path.getPoint(0);
        assertEquals(zoomedStart.x, actualStart.x, DOUBLE_DELTA);
        assertEquals(zoomedStart.y, actualStart.y, DOUBLE_DELTA);


        Point2D.Double zoomedEnd =  ZoomController.getZoomedValue(end, zoom);
        Point2D.Double actualEnd = path.getPoint(1);
        assertEquals(zoomedEnd.x, actualEnd.x, DOUBLE_DELTA);
        assertEquals(zoomedEnd.y, actualEnd.y, DOUBLE_DELTA);
    }
}
