package pipe.controllers;

import pipe.historyActions.MultipleEdit;
import pipe.historyActions.component.ChangePetriNetComponentName;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract parent class of component controllers.
 *
 * Contains a set of methods that is common to them all and is responsible for registering
 * undo edits.
 * @param <T> component model class
 */
public abstract class AbstractPetriNetComponentController<T extends PetriNetComponent> {
    /**
     * Underlying model
     */
    protected final T component;

    /**
     * Listener for undo/redo actions being created
     */
    protected final UndoableEditListener listener;

    /**
     * Set to true if multiple UndoEdit registers should be combined into one action.
     */
    private boolean registerMultipleEdits = false;

    /**
     * When registerMultipleEdits is set to true any registered edits are built up in this list
     */
    private List<UndoableEdit> multipleEdits = new LinkedList<>();

    /**
     * Constructor
     * @param component underlying Petri net controller
     * @param listener undo listener
     */
    protected AbstractPetriNetComponentController(T component, UndoableEditListener listener) {
        this.component = component;
        this.listener = listener;
    }

    /**
     *
     *
     * @param newId new id for the Petri net, must be unique
     */
    public final void setId(String newId) {
        String oldId = component.getId();
        if (!oldId.equals(newId)) {
            component.setId(newId);
            registerUndoableEdit(new ChangePetriNetComponentName(component, oldId, newId));
        }
    }

    /**
     * Registers the edit with the listener
     *
     * @param edit to register 
     */
    protected final void registerUndoableEdit(UndoableEdit edit) {
        if (registerMultipleEdits) {
            multipleEdits.add(edit);
        } else {
            listener.undoableEditHappened(new UndoableEditEvent(this, edit));
        }
    }

    /**
     * Any changes made to the Petri net controller will be built up as a
     * multiple edit.
     * <p>
     * You will need to call finishMultipleEdits() to commit these changes to
     * the undo listener
     * </p>
     */
    public final void startMultipleEdits() {
        multipleEdits.clear();
        registerMultipleEdits = true;
    }

    /**
     * Commits any edits that have been registered via registerUndoableEdit since
     * startMultipleEdits was called to the listener
     */
    public final void finishMultipleEdits() {
        registerMultipleEdits = false;
        if (!multipleEdits.isEmpty()) {
            registerUndoableEdit(new MultipleEdit(multipleEdits));
        }
        multipleEdits.clear();
    }

}
