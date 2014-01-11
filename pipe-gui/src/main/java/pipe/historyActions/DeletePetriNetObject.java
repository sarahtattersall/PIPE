/*
 * DeletePetriNetObjectEdit.java
 */
package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.component.PetriNetComponent;


/**
 * @author Pere Bonet
 */
public class DeletePetriNetObject
        extends HistoryItem {

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
    public void redo() {
        petriNet.remove(component);
    }


    /** */
    public void undo() {
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
