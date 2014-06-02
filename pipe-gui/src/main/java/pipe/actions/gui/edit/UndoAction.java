package pipe.actions.gui.edit;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import javax.swing.undo.UndoManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UndoAction extends GuiAction {

    private final PipeApplicationController applicationController;
    private RedoAction redoAction;

    public UndoAction(PipeApplicationController applicationController) {
        super("Undo", "Undo (Ctrl-Z)", KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
    }

    /**
     * Register a redo action to this undo actions
     * @param redoAction
     */
    public void registerRedoAction(RedoAction redoAction) {
        this.redoAction = redoAction;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PetriNetController controller = applicationController.getActivePetriNetController();
        UndoManager manager = controller.getUndoManager();
        manager.undo();

        this.setEnabled(manager.canUndo());
        if (redoAction != null) {
            redoAction.setEnabled(manager.canRedo());
        }
    }
}