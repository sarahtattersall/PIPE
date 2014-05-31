/*
 * AddPetriNetObjectEdit.java
 */

package pipe.historyActions.component;


import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author corveau
 */
public class AddPetriNetObject extends AbstractUndoableEdit {

    private final PetriNetComponent component;

    private final PetriNet petriNet;


    /**
     * Creates a new instance of placeWeightEdit
     *
     * @param component
     * @param petriNet
     */
    public AddPetriNetObject(PetriNetComponent component, PetriNet petriNet) {
        this.component = component;
        this.petriNet = petriNet;
    }


    /** */
    @Override
    public void undo() {
        super.undo();
        try {
            petriNet.remove(component);
        } catch (PetriNetComponentException e) {
            e.printStackTrace();
        }
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        try {
            petriNet.add(component);
        } catch (PetriNetComponentException e) {
            e.printStackTrace();
        }
    }


    public String toString() {
        return super.toString() + " \"" + component + "\"";
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
