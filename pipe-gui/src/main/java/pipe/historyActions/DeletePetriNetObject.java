/*
 * DeletePetriNetObjectEdit.java
 */
package pipe.historyActions;

import pipe.models.petrinet.PetriNet;
import pipe.models.component.PetriNetComponent;

import javax.swing.undo.AbstractUndoableEdit;


/**
 * @author Pere Bonet
 */
public class DeletePetriNetObject
        extends AbstractUndoableEdit {

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
        petriNet.remove(component);
    }


    /** */
    @Override
    public void undo() {
        super.undo();
        petriNet.add(component);
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
