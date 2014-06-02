package pipe.views;

import matchers.component.PropertyChangeUtils;
import org.junit.Test;
import pipe.actions.gui.ZoomManager;
import pipe.actions.gui.ZoomUI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ZoomUITest {
    private final static double MAX_ZOOM = 3;

    private final static double MIN_ZOOM = 0.6;

    private final static double ZOOM_INCREMENT = 0.1;

    @Test
    public void returnsCorrectPercentage() {
        ZoomManager zoomUI = new ZoomUI(1, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        assertEquals(100, zoomUI.getPercentageZoom());
    }

    @Test
    public void returnsCorrectPercentageForNonIntegerScale() {
        ZoomManager zoomUI = new ZoomUI(1.2, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        assertEquals(120, zoomUI.getPercentageZoom());
    }

    @Test
    public void canZoomOutIfGreaterThanMinValue() {
        double scale = MIN_ZOOM + ZOOM_INCREMENT;
        ZoomManager zoomUI = new ZoomUI(scale, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        assertTrue(zoomUI.canZoomOut());
    }

    @Test
    public void zoomsOutIfGreaterThanMinValue() {
        double scale = MIN_ZOOM + ZOOM_INCREMENT;
        ZoomManager zoomUI = new ZoomUI(scale, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.zoomOut();
        assertEquals(toPercentage(scale - ZOOM_INCREMENT), zoomUI.getPercentageZoom());
    }

    private int toPercentage(double zoomScale) {
        return (int) (zoomScale * 100);
    }

    @Test
    public void cantZoomOutIfMinValue() {
        ZoomManager zoomUI = new ZoomUI(MIN_ZOOM, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        assertFalse(zoomUI.canZoomOut());
    }

    @Test
    public void wontZoomOutIfMinValue() {
        ZoomManager zoomUI = new ZoomUI(MIN_ZOOM, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.zoomOut();
        assertEquals(toPercentage(MIN_ZOOM), zoomUI.getPercentageZoom());
    }

    @Test
    public void canZoomInIfGreaterThanMaxValue() {
        double scale = MAX_ZOOM - ZOOM_INCREMENT;
        ZoomManager zoomUI = new ZoomUI(scale, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        assertTrue(zoomUI.canZoomIn());
    }

    @Test
    public void zoomsInIfLessThanMaxValue() {
        double scale = MAX_ZOOM - ZOOM_INCREMENT;
        ZoomManager zoomUI = new ZoomUI(scale, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.zoomIn();
        assertEquals(toPercentage(scale + ZOOM_INCREMENT), zoomUI.getPercentageZoom());
    }

    @Test
    public void cantZoomInIfMaxValue() {
        ZoomManager zoomUI = new ZoomUI(MAX_ZOOM, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        assertFalse(zoomUI.canZoomIn());
    }

    @Test
    public void wontZoomInIfMaxValue() {
        ZoomManager zoomUI = new ZoomUI(MAX_ZOOM, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.zoomIn();
        assertEquals(toPercentage(MAX_ZOOM), zoomUI.getPercentageZoom());
    }

    @Test
    public void wontFireChangeIfCantZoomIn() {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        ZoomUI zoomUI = new ZoomUI(MAX_ZOOM, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.addPropertyChangeListener(listener);
        zoomUI.zoomIn();
        verify(listener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void wontFireChangeIfCantZoomOut() {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        ZoomUI zoomUI = new ZoomUI(MIN_ZOOM, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.addPropertyChangeListener(listener);
        zoomUI.zoomOut();
        verify(listener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void firesChangeOnZoomIn() {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        ZoomUI zoomUI = new ZoomUI(1, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.addPropertyChangeListener(listener);
        zoomUI.zoomIn();
        verify(listener).propertyChange(argThat(PropertyChangeUtils.hasValues(ZoomUI.ZOOM_IN_CHANGE_MESSAGE, 1.0, 1.1)));
    }

    @Test
    public void firesChangeOnZoomOut() {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        ZoomUI zoomUI = new ZoomUI(1, ZOOM_INCREMENT, MAX_ZOOM, MIN_ZOOM, null);
        zoomUI.addPropertyChangeListener(listener);
        zoomUI.zoomOut();
        verify(listener).propertyChange(argThat(PropertyChangeUtils.hasValues(ZoomUI.ZOOM_OUT_CHANGE_MESSAGE, 1.0, 0.9)));
    }
}
