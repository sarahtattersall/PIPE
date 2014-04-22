package pipe.gui;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;

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
    public void selectsUsingLocationOnMousePress() {
        MouseEvent e = mock(MouseEvent.class);
        when(e.getButton()).thenReturn(MouseEvent.BUTTON1);
        when(e.isControlDown()).thenReturn(false);

        Point clickPoint = new Point(50, 50);
        when(e.getPoint()).thenReturn(clickPoint);
        manager.mousePressed(e);

        int x = (int) clickPoint.getX();
        int y = (int) clickPoint.getY();
        Rectangle rect = new Rectangle(x, y, 0, 0);
        verify(mockController).select(rect);
    }


    @Test
    public void selectsLocationOnMouseDrag() {
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

        double x = clickPoint.getX();
        double y = clickPoint.getY();
        double w = dragPoint.getX() - clickPoint.getX();
        double h = dragPoint.getY() - clickPoint.getY();
        Rectangle rect = new Rectangle((int) x, (int) y, (int) w, (int) h);
        verify(mockController, times(2)).select(rect);
    }
}

