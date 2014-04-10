package pipe.controllers;

import pipe.historyActions.PetriNetObjectName;
import pipe.models.component.PetriNetComponent;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

public abstract class AbstractPetriNetComponentController<T extends PetriNetComponent> {
    protected final T component;


    protected final UndoableEditListener listener;

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
     *
     * Registers the edit with the listener
     *
     * @param edit
     */
    protected void registerUndoableEdit(UndoableEdit edit) {
        listener.undoableEditHappened(new UndoableEditEvent(this, edit));
    }

}
