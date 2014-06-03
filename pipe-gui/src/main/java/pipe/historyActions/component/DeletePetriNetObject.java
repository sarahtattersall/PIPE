/*
 * DeletePetriNetObjectEdit.java
 */
package pipe.historyActions.component;


import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.undo.AbstractUndoableEdit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Pere Bonet
 */
public class DeletePetriNetObject
        extends AbstractUndoableEdit {

    private static final Logger LOGGER = Logger.getLogger(DeletePetriNetObject.class.getName());

    private PetriNetComponent component;
    private final PetriNet petriNet;


    /**
     * @param component
     * @param petriNet
     */
    public DeletePetriNetObject(PetriNetComponent component, PetriNet petriNet) {
        this.component = component;
        this.petriNet = petriNet;
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        try {
            petriNet.remove(component);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


    /** */
    @Override
    public void undo() {
        super.undo();
        try {
            petriNet.add(component);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


    public String toString() {
        return super.toString() + " " + component.getClass().getSimpleName()
                + " [" + component + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeletePetriNetObject that = (DeletePetriNetObject) o;

        if (!component.equals(that.component)) return false;
        if (!petriNet.equals(that.petriNet)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = component.hashCode();
        result = 31 * result + petriNet.hashCode();
        return result;
    }
}
