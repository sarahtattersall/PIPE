package pipe.utilities.gui;

import pipe.controllers.PetriNetController;
import pipe.gui.ZoomController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GuiUtils {

    public static Point getAccurateMouseLocation(Component component, MouseEvent mouseEvent,
                                                      PetriNetController petriNetController) {
        MouseEvent accurateEvent = SwingUtilities.convertMouseEvent(mouseEvent.getComponent(), mouseEvent, component);
        Point point = accurateEvent.getPoint();
        return getUnZoomedPoint(point, petriNetController);
    }

    /**
     *
     * @param point mouse point
     * @param petriNetController
     * @return unzoomed point locations
     */
    public static Point getUnZoomedPoint(Point point, PetriNetController petriNetController) {
        ZoomController controller = petriNetController.getZoomController();
        return controller.getUnzoomedValue(point);
    }
}
