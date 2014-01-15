package pipe.utilities.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GuiUtils {

    public static MouseEvent getAccurateMouseEvent(Component component, MouseEvent mouseEvent) {
        MouseEvent accurateEvent = SwingUtilities.convertMouseEvent(mouseEvent.getComponent(), mouseEvent, component);
        return accurateEvent;
    }
}
