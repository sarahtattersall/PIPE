package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CloseWindowAction extends GuiAction {

    private final PipeApplicationController applicationController;

    public CloseWindowAction(PipeApplicationController applicationController) {
        super("Close", "Close the current tab", KeyEvent.VK_W, InputEvent.META_DOWN_MASK);
        this.applicationController = applicationController;
    }

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
