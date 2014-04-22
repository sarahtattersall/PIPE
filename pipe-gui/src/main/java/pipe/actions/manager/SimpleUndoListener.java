package pipe.actions.manager;

import pipe.controllers.PipeApplicationController;

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

    private ComponentEditorManager componentEditorManager;

    private PipeApplicationController applicationController;

    public SimpleUndoListener(ComponentEditorManager componentEditorManager, PipeApplicationController applicationController) {
        this.componentEditorManager = componentEditorManager;
        this.applicationController = applicationController;
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {

        UndoManager undoManager = applicationController.getActivePetriNetController().getUndoManager();
        undoManager.addEdit(e.getEdit());
        componentEditorManager.updateButtons();
    }
}
