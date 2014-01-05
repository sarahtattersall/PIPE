package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.ZoomController;
import pipe.historyActions.HistoryItem;
import pipe.models.component.Arc;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.NameLabel;

public class ArcViewTest {

    private ArcView arcView;
    private PetriNetController mockController;
    private Arc mockArc;
    private Point2D.Double start = new Point2D.Double(50, 39);
    private Point2D.Double end = new Point2D.Double(100, 500);
    private static final double DOUBLE_DELTA = 0.0001;

    @Before
    public void setUp() throws Exception {
        mockController = mock(PetriNetController.class);
        mockArc = mock(Arc.class);
        when(mockArc.getStartPoint()).thenReturn(start);
        when(mockArc.getEndPoint()).thenReturn(end);
        arcView = new NormalArcView(mockArc, mockController);
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

    private class TestingArcView extends NormalArcView {
        private static final long serialVersionUID = 1L;

        public TestingArcView(ConnectableView newSource) {
            super(newSource);
        }

        @Override
        protected void updateHistory(HistoryItem historyItem) {
        }

        @Override
        protected void removeLabelFromParentContainer(NameLabel label) {
        }
    }
}
