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

    @Before
    public void setUp() {
        mockController = mock(PetriNetController.class);
        tab = mock(PetriNetTab.class);
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
        int zoom = 120;
        manager.setZoom(zoom);

        MouseEvent e = mock(MouseEvent.class);
        when(e.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(e.isControlDown()).thenReturn(false);

        Point clickPoint = new Point(50, 50);
        when(e.getPoint()).thenReturn(clickPoint);
        manager.mousePressed(e);

        int x = ZoomController.getUnzoomedValue((int) clickPoint.getX(), zoom);
        int y = ZoomController.getUnzoomedValue((int) clickPoint.getY(), zoom);
        Rectangle unzoomedRect = new Rectangle(x, y, 0, 0);
        verify(mockController).select(unzoomedRect);
    }


    @Test
    public void selectsUsingUnZoomedLocationOnMouseDrag() {
        int zoom = 120;
        manager.setZoom(zoom);

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

        double x = ZoomController.getUnzoomedValue(clickPoint.getX(), zoom);
        double y = ZoomController.getUnzoomedValue(clickPoint.getY(), zoom);
        double w = ZoomController.getUnzoomedValue(dragPoint.getX() - clickPoint.getX(), zoom);
        double h = ZoomController.getUnzoomedValue(dragPoint.getY() - clickPoint.getY(), zoom);
        Rectangle unzoomedRect = new Rectangle((int) x, (int) y, (int) w, (int) h);
        verify(mockController).select(unzoomedRect);
    }
}

