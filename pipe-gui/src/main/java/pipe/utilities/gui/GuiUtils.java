package pipe.utilities.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Static class with useful utility methods for the gui
 */
public final class GuiUtils {

    /**
     * Private constructor for static class
     */
    private GuiUtils() {}

    /**
     *
     * Pops up error message dialog
     *
     * @param message error message
     */
    public static void displayErrorMessage(Component parent, String message) {

        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.YES_NO_OPTION);
    }
}
