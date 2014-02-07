package pipe.actions.edit;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Pastes selected items in the Petri net
 */
public class PasteAction extends GuiAction {

    private final PipeApplicationController applicationController;

    public PasteAction(PipeApplicationController applicationController) {
        super("Paste", "Paste (Ctrl-V)", KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        petriNetController.paste();
    }
}