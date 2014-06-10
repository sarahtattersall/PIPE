/*
 * DeletePetriNetObjectEdit.java
 */
package pipe.historyActions.component;


import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.undo.AbstractUndoableEdit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Deletes a Petri net component from the a net
 */
public class DeletePetriNetObject extends AbstractUndoableEdit {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(DeletePetriNetObject.class.getName());

    /**
     * Petri net to delete component from
     */
    private final PetriNet petriNet;

    /**
     * Component that has been deleted from the Petri net
     */
    private PetriNetComponent component;


    /**
     *
     * Constructor
     *
     * @param component Petri net component that has been deleted from the Petri net
     * @param petriNet Petri net remove/add the component to for undo/redo features
     */
    public DeletePetriNetObject(PetriNetComponent component, PetriNet petriNet) {
        this.component = component;
        this.petriNet = petriNet;
    }

    /**
     * Adds the component to the Petri net
     */
    @Override
    public void undo() {
        super.undo();
        try {
            petriNet.add(component);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Deletes the component from the Petri net
     */
    @Override
    public void redo() {
        super.redo();
        try {
            petriNet.remove(component);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public int hashCode() {
        int result = component.hashCode();
        result = 31 * result + petriNet.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeletePetriNetObject that = (DeletePetriNetObject) o;

        if (!component.equals(that.component)) {
            return false;
        }
        if (!petriNet.equals(that.petriNet)) {
            return false;
        }

        return true;
    }
}
