package pipe.handlers.mouse;

import java.awt.event.MouseEvent;

/**
 * This interace provides commonly used methods to test mouse events
 */
public interface MouseUtilities {
    /**
     *
     * @param event
     * @return true if left mouse pressed
     */
    boolean isLeftMouse(MouseEvent event);
}
