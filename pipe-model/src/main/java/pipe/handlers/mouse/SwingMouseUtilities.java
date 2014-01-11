package pipe.handlers.mouse;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * A Swing implementation of MouseUtilities
 */
public class SwingMouseUtilities implements MouseUtilities {
    @Override
    public boolean isLeftMouse(MouseEvent event) {
        return SwingUtilities.isLeftMouseButton(event);
    }
}
