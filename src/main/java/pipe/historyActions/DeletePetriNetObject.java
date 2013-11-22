/*
 * DeletePetriNetObjectEdit.java
 */
package pipe.historyActions;

import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.PetriNetComponent;
import pipe.views.PetriNetView;


/**
 * @author Pere Bonet
 */
public class DeletePetriNetObject
        extends HistoryItem
{

    private PetriNetComponent component;
    private final PetriNet petriNet;
    private final PetriNetTab tab;


    /**
     * Creates a new instance of placeWeightEdit
     * @param component
     * @param tab
     * @param petriNet
     */
    public DeletePetriNetObject(PetriNetComponent component, PetriNetTab tab, PetriNet petriNet)
    {
        this.component = component;
        this.tab = tab;
        this.petriNet = petriNet;
//        this.component.markAsDeleted();
    }


    /** */
    public void redo()
    {

        petriNet.remove(component);
    }


    /** */
    public void undo()
    {
        petriNet.add(component);
//        component.undelete(_model, tab);
    }


    public String toString()
    {
        return super.toString() + " " + component.getClass().getSimpleName()
                + " [" + component + "]";
    }

}
