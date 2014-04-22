package pipe.actions.gui.edit;

import pipe.actions.gui.GuiAction;
import pipe.actions.manager.ComponentCreatorManager;
import pipe.actions.manager.ComponentEditorManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UndoAction extends GuiAction {

    private final PipeApplicationController applicationController;

    private final ComponentEditorManager container;

    public UndoAction(PipeApplicationController applicationController, ComponentEditorManager container) {
        super("Undo", "Undo (Ctrl-Z)", KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.container = container;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PetriNetController controller = applicationController.getActivePetriNetController();
        controller.getUndoManager().undo();
        container.updateButtons();
    }
}