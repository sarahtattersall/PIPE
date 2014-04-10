package pipe.actions.gui.edit;

import pipe.actions.gui.GuiAction;
import pipe.actions.manager.ComponentEditorManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RedoAction extends GuiAction {

    private final PipeApplicationController applicationController;

    private final ComponentEditorManager container;

    public RedoAction(PipeApplicationController applicationController, ComponentEditorManager container) {
        super("Redo", "Redo (Ctrl-Y)",  KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.container = container;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PetriNetController controller = applicationController.getActivePetriNetController();
        controller.getUndoManager().redo();
        container.updateButtons();
    }

}