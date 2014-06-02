package pipe.actions.gui.edit;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import javax.swing.undo.UndoManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RedoAction extends GuiAction {

    private final PipeApplicationController applicationController;

    private final UndoAction undoAction;

    public RedoAction(PipeApplicationController applicationController, UndoAction undoAction) {
        super("Redo", "Redo (Ctrl-Y)",  KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.undoAction = undoAction;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PetriNetController controller = applicationController.getActivePetriNetController();
        UndoManager manager = controller.getUndoManager();
        manager.redo();

        this.setEnabled(manager.canRedo());
        undoAction.setEnabled(manager.canUndo());
    }

}