package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CloseWindowAction extends GuiAction {
    private final PipeApplicationView pipeApplicationView;

    private final PipeApplicationController applicationController;

    public CloseWindowAction(PipeApplicationView pipeApplicationView, PipeApplicationController applicationController) {
        super("Close", "Close the current tab", KeyEvent.VK_W, InputEvent.META_DOWN_MASK);
        this.pipeApplicationView = pipeApplicationView;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!applicationController.hasCurrentPetriNetChanged()) {
            pipeApplicationView.removeCurrentTab();
        } else {
            int result = JOptionPane.showConfirmDialog(pipeApplicationView,  "Do you really want to close this Petri net? It has unsaved changes.",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                pipeApplicationView.removeCurrentTab();
            }
        }
    }
}
