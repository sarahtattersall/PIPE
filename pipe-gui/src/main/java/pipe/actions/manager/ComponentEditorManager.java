package pipe.actions.manager;

import pipe.actions.gui.*;
import pipe.controllers.application.PipeApplicationController;

import javax.swing.event.UndoableEditListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * These components are responsible for editing and managing items
 * on the petri net canvas when pressed
 */
public class ComponentEditorManager implements ActionManager {
    /**
     * Action copies whatever is selected at the time
     */
    private final CopyAction copyAction;

    /**
     * Action taht cuts whatever is selected
     */
    private final CutAction cutAction;

    /**
     * Action that pastes the current selection
     */
    private final PasteAction pasteAction;

    /**
     * Action that deletes the current selection
     */
    private final DeleteAction deleteAction;

    /**
     * Action that undoes the last edit
     */
    public final UndoAction undoAction;


    /**
     * Action that redoes the previous undo
     */
    public final RedoAction redoAction;

    private Map<GuiAction, Boolean> editEnabledStatus = new HashMap<>();

    /**
     * Creates actions for editing the petri net
     *
     * @param controller PIPE application controller
     */
    public ComponentEditorManager(PipeApplicationController controller) {
        copyAction = new CopyAction(controller);
        pasteAction = new PasteAction(controller);
        cutAction = new CutAction(controller);
        deleteAction = new DeleteAction(controller);
        undoAction = new UndoAction(controller);
        redoAction = new RedoAction(controller, undoAction);
        undoAction.registerRedoAction(redoAction);
        undoAction.setEnabled(false);
        redoAction.setEnabled(false);

        UndoableEditListener listener = new SimpleUndoListener(redoAction, undoAction, controller);
        deleteAction.addUndoableEditListener(listener);
        copyAction.addUndoableEditListener(listener);
        cutAction.addUndoableEditListener(listener);
        pasteAction.addUndoableEditListener(listener);

        storeEnabledStatus();
    }

    /**
     * @return inorder iterable of the actions this class is responsible for managing
     */
    @Override
    public Iterable<GuiAction> getActions() {
        editEnabledStatus.keySet();
        return Arrays.asList(cutAction, copyAction, pasteAction, deleteAction, undoAction, redoAction);
    }

    /**
     * Disables actions and stores their current state
     * ready for re-enabling
     */
    @Override
    public void disableActions() {
        storeEnabledStatus();
        for (GuiAction action : getActions()) {
            action.setEnabled(false);
        }
    }

    /**
     * Restores actions to their previous states
     */
    @Override
    public void enableActions() {
        for (GuiAction action : getActions()) {
            action.setEnabled(editEnabledStatus.get(action));
        }
    }

    /**
     * Stores the current actions enabled status in the map
     */
    private void storeEnabledStatus() {
        for (GuiAction action : getActions()) {
            editEnabledStatus.put(action, action.isEnabled());
        }
    }
}
