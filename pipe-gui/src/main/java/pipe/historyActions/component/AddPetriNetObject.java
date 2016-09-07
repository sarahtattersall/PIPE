/*
 * AddPetriNetObjectEdit.java
 */

package pipe.historyActions.component;


import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.undo.AbstractUndoableEdit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adds a Petri net component to the Petri net
 */
@SuppressWarnings("serial")
public class AddPetriNetObject extends AbstractUndoableEdit {
    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(AddPetriNetObject.class.getName());

    /**
     * New petri net component
     */
    private final PetriNetComponent component;

    /**
     * Petri net that houses the component
     */
    private final PetriNet petriNet;


    /**
     * Creates a new instance of placeWeightEdit
     *
     * @param component new Petri net component
     * @param petriNet petri net that houses the component
     */
    public AddPetriNetObject(PetriNetComponent component, PetriNet petriNet) {
        this.component = component;
        this.petriNet = petriNet;
    }


    /**
     * Removes the component from the Petri net
     */
    @Override
    public final void undo() {
        super.undo();
        try {
            petriNet.remove(component);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }


    /**
     * Adds the component back to the Petri net
     */
    @Override
    public final void redo() {
        super.redo();
        try {
            petriNet.add(component);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddPetriNetObject that = (AddPetriNetObject) o;

        if (component != null ? !component.equals(that.component) : that.component != null) {
            return false;
        }
        if (petriNet != null ? !petriNet.equals(that.petriNet) : that.petriNet != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = component != null ? component.hashCode() : 0;
        result = 31 * result + (petriNet != null ? petriNet.hashCode() : 0);
        return result;
    }
}
