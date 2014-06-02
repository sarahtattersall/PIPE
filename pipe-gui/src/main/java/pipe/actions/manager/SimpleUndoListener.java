package pipe.actions.manager;

import pipe.actions.gui.RedoAction;
import pipe.actions.gui.UndoAction;
import pipe.controllers.application.PipeApplicationController;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 * Simple undo listener that will register events with the active Petri nets undo manager
 *
 * It is mainly for use with {@link pipe.actions.gui.GuiAction} classes so that they can
 * register undo events when performing their actions
 */
public class SimpleUndoListener implements UndoableEditListener {

    private final RedoAction redoAction;

    private final UndoAction undoAction;

    private PipeApplicationController applicationController;

    public SimpleUndoListener(RedoAction redoAction, UndoAction undoAction, PipeApplicationController applicationController) {
        this.redoAction = redoAction;
        this.undoAction = undoAction;
        this.applicationController = applicationController;
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        UndoManager undoManager = applicationController.getActivePetriNetController().getUndoManager();
        undoManager.addEdit(e.getEdit());
        redoAction.setEnabled(undoManager.canRedo());
        undoAction.setEnabled(undoManager.canUndo());

    }
}
