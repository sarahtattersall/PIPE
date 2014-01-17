package pipe.gui;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectionManagerTest {

    SelectionManager manager;
    PetriNetTab tab;
    PetriNetController mockController;
    ZoomController zoomController;

    @Before
    public void setUp() {
        mockController = mock(PetriNetController.class);
        zoomController = new ZoomController(100);
        tab = mock(PetriNetTab.class);
        when(tab.getZoomController()).thenReturn(zoomController);
        manager = new SelectionManager(tab, mockController);
    }

    @Test
    public void deselectCallsController() {
        manager.clearSelection();
        verify(mockController).deselectAll();
    }

    @Test
    public void translateSelectionCallsController() {
        manager.translateSelection(5,10);
        verify(mockController).translateSelected(new Point2D.Double(5, 10));
    }

    @Test
    public void selectsUsingUnZoomedLocationOnMousePress() {
        zoomController.setZoom(120);
        MouseEvent e = mock(MouseEvent.class);
        when(e.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(e.isControlDown()).thenReturn(false);

        Point clickPoint = new Point(50, 50);
        when(e.getPoint()).thenReturn(clickPoint);
        manager.mousePressed(e);

        int x = zoomController.getUnzoomedValue((int) clickPoint.getX());
        int y = zoomController.getUnzoomedValue((int) clickPoint.getY());
        Rectangle unzoomedRect = new Rectangle(x, y, 0, 0);
        verify(mockController).select(unzoomedRect);
    }


    @Test
    public void selectsUsingUnZoomedLocationOnMouseDrag() {
        zoomController.setZoom(120);

        MouseEvent clickEvent = mock(MouseEvent.class);
        when(clickEvent.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(clickEvent.isControlDown()).thenReturn(false);

        Point clickPoint = new Point(50, 50);
        when(clickEvent.getPoint()).thenReturn(clickPoint);
        manager.mousePressed(clickEvent);

        MouseEvent dragEvent = mock(MouseEvent.class);

        Point dragPoint = new Point(160, 170);
        when(dragEvent.getPoint()).thenReturn(dragPoint);
        when(dragEvent.getX()).thenReturn(dragPoint.x);
        when(dragEvent.getY()).thenReturn(dragPoint.y);

        manager.mouseDragged(dragEvent);

        double x = zoomController.getUnzoomedValue(clickPoint.getX());
        double y = zoomController.getUnzoomedValue(clickPoint.getY());
        double w = zoomController.getUnzoomedValue(dragPoint.getX() - clickPoint.getX());
        double h = zoomController.getUnzoomedValue(dragPoint.getY() - clickPoint.getY());
        Rectangle unzoomedRect = new Rectangle((int) x, (int) y, (int) w, (int) h);
        verify(mockController).select(unzoomedRect);
    }
}

