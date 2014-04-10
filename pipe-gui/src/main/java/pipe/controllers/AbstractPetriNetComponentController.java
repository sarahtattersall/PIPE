package pipe.controllers;

import pipe.historyActions.MultipleEdit;
import pipe.historyActions.PetriNetObjectName;
import pipe.models.component.PetriNetComponent;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPetriNetComponentController<T extends PetriNetComponent> {
    protected final T component;

    protected final UndoableEditListener listener;

    /**
     * Set to true if multiple UndoEdit registers should be combined into one action.
     */
    private boolean registerMultipleEdits = false;

    /**
     * When registerMultipleEdits is set to true any registered edits are built up in this list
     */
    private List<UndoableEdit> multipleEdits = new LinkedList<>();

    protected AbstractPetriNetComponentController(T component, UndoableEditListener listener) {
        this.component = component;
        this.listener = listener;
    }

    public void setName(String newName) {
        String oldName = component.getId();
        component.setName(newName);
        component.setId(newName);
        registerUndoableEdit(new PetriNetObjectName(component, oldName, newName));
    }

    /**
     * Registers the edit with the listener
     *
     * @param edit
     */
    protected void registerUndoableEdit(UndoableEdit edit) {
        if (registerMultipleEdits) {
            multipleEdits.add(edit);
        } else {
            listener.undoableEditHappened(new UndoableEditEvent(this, edit));
        }
    }

    /**
     * Any changes made to the Petri net controller will be built up as a
     * multiple edit.
     * <p/>
     * You will need to call finishMultipleEdits() to commit these changes to
     * the undo listener
     */
    public void startMultipleEdits() {
        multipleEdits.clear();
        registerMultipleEdits = true;
    }

    /**
     * Commits any edits that have been registered via registerUndoableEdit since
     * startMultipleEdits was called to the listener
     */
    public void finishMultipleEdits() {
        registerMultipleEdits = false;
        if (!multipleEdits.isEmpty()) {
            registerUndoableEdit(new MultipleEdit(multipleEdits));
        }
        multipleEdits.clear();
    }

}
