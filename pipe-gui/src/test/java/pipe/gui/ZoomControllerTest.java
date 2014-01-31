package pipe.gui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZoomControllerTest {
    ZoomController controller;

    @Test
    public void canZoomOutIfGreaterThanMinValue() {
        controller = new ZoomController(Constants.ZOOM_MIN + Constants.ZOOM_DELTA);
        assertTrue(controller.canZoomOut());
    }

    @Test
    public void zoomsOutIfGreaterThanMinValue() {
        int percent = Constants.ZOOM_MIN + Constants.ZOOM_DELTA;
        controller = new ZoomController(percent);
        controller.zoomOut();
        assertEquals(percent - Constants.ZOOM_DELTA, controller.getPercent());
    }


    @Test
    public void cantZoomOutIfMinValue() {
        controller = new ZoomController(Constants.ZOOM_MIN);
        assertFalse(controller.canZoomOut());
    }

    @Test
    public void wontZoomOutIfMinValue() {
        controller = new ZoomController(Constants.ZOOM_MIN);
        controller.zoomOut();
        assertEquals(Constants.ZOOM_MIN, controller.getPercent());
    }

    @Test
    public void canZoomInIfGreaterThanMaxValue() {
        controller = new ZoomController(Constants.ZOOM_MAX - Constants.ZOOM_DELTA);
        assertTrue(controller.canZoomIn());
    }

    @Test
    public void zoomsInIfLessThanMaxValue() {
        int percent = Constants.ZOOM_MAX - Constants.ZOOM_DELTA;
        controller = new ZoomController(percent);
        controller.zoomIn();
        assertEquals(percent + Constants.ZOOM_DELTA, controller.getPercent());
    }


    @Test
    public void cantZoomInIfMaxValue() {
        controller = new ZoomController(Constants.ZOOM_MAX);
        assertFalse(controller.canZoomIn());
    }

    @Test
    public void wontZoomInIfMaxValue() {
        controller = new ZoomController(Constants.ZOOM_MAX);
        controller.zoomIn();
        assertEquals(Constants.ZOOM_MAX, controller.getPercent());
    }

}
