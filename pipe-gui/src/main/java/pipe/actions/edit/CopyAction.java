package pipe.actions.edit;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Responsible for copying selected items on the Petri net
 */
public class CopyAction extends GuiAction {
    private final PipeApplicationController applicationController;

    public CopyAction(PipeApplicationController applicationController) {
        super("Copy", "Copy (Ctrl-C)", KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        petriNetController.copySelection();
    }
}
