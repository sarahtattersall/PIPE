package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import javax.swing.undo.UndoManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action used to perform an undo whilst in edit more
 */
public class UndoAction extends GuiAction {

    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Redo action
     */
    private RedoAction redoAction;

    /**
     *
     * Constructor
     * @param applicationController main PIPE application controller
     */
    public UndoAction(PipeApplicationController applicationController) {
        super("Undo", "Undo (Ctrl-Z)", KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
    }

    /**
     * Register a redo action to this undo actions
     *
     * @param redoAction
     */
    public void registerRedoAction(RedoAction redoAction) {
        this.redoAction = redoAction;
    }

    /**
     * Perform an undo and enable the redo action if it has been set
     * @param actionEvent
     */
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