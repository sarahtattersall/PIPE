package pipe.actions.gui.edit;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.historyActions.MultipleEdit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CutAction extends GuiAction {
    private final PipeApplicationController applicationController;

    public CutAction(PipeApplicationController applicationController) {
        super("Cut", "Cut (Ctrl-X)", KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        petriNetController.copySelection();
        registerUndoEvent(new MultipleEdit(petriNetController.deleteSelection()));
    }
}