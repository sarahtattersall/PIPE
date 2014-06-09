package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Action that closes the currently displayed tab
 */
public class CloseWindowAction extends GuiAction {

    /**
     * Application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Constructor
     * @param applicationController PIPE main application controller
     */
    public CloseWindowAction(PipeApplicationController applicationController) {
        super("Close", "Close the current tab", KeyEvent.VK_W, InputEvent.META_DOWN_MASK);
        this.applicationController = applicationController;
    }

    /**
     * On performing this action the currently displayed tab will be closed.
     *
     * If there have been modifications to the Petri net in the current tab then a confirm
     * dialog is shown asking the user if they really wish to close.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!applicationController.hasCurrentPetriNetChanged()) {
            applicationController.removeActiveTab();
        } else {
            int result = JOptionPane.showConfirmDialog(null,
                    "Do you really want to close this Petri net? It has unsaved changes.", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                applicationController.removeActiveTab();
            }
        }
    }
}
