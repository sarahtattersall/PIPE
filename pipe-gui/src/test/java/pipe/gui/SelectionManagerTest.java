package pipe.gui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.controllers.PetriNetController;
import pipe.controllers.SelectionManager;

@RunWith(MockitoJUnitRunner.class)
public class SelectionManagerTest {

    SelectionManager manager;

    @Mock
    PetriNetTab tab;

    @Mock
    PetriNetController mockController;

    @Before
    public void setUp() {
        when(mockController.getPetriNetTab()).thenReturn(tab);
        manager = new SelectionManager(mockController);
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

