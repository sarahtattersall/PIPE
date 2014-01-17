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


    @Test
    public void correctlyZoomsInt() {
        int zoomPercent = 130;
        controller = new ZoomController(zoomPercent);

        int zoomValue = 50;
        int actual = controller.getZoomedValue(zoomValue);
        int expected = (int) (zoomValue * zoomPercent * 0.01);
        assertEquals("Did not correctly zoom value", expected, actual);
    }

    @Test
    public void correctlyZoomsDouble() {
        int zoomPercent = 130;
        controller = new ZoomController(zoomPercent);

        double zoomValue = 50.6;
        double actual = controller.getZoomedValue(zoomValue);
        double expected = zoomValue * zoomPercent * 0.01;
        assertEquals("Did not correctly zoom value", expected, actual, 0.001);
    }

    @Test
    public void unzoomsInt() {
        int zoomPercent = 130;
        controller = new ZoomController(zoomPercent);

        int zoomValue = 50;
        int actual = controller.getUnzoomedValue(zoomValue);
        int expected = (int) (zoomValue / (zoomPercent * 0.01));
        assertEquals(expected, actual);
    }

    @Test
    public void unzoomsDouble() {
        int zoomPercent = 130;
        controller = new ZoomController(zoomPercent);

        double zoomValue = 50.6;
        double actual = controller.getUnzoomedValue(zoomValue);
        double expected = (zoomValue / (zoomPercent * 0.01));
        assertEquals(expected, actual, 0.01);
    }

}
