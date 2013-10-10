/*
 * DeletePetriNetObjectEdit.java
 */
package pipe.historyActions;

import pipe.gui.PetriNetTab;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;


/**
 * @author Pere Bonet
 */
public class DeletePetriNetObject
        extends HistoryItem
{

    private PetriNetViewComponent _viewComponent;
    private final PetriNetView _model;
    private final PetriNetTab _view;


    /**
     * Creates a new instance of placeWeightEdit
     * @param viewComponent
     * @param tab
     * @param view
     */
    public DeletePetriNetObject(PetriNetViewComponent viewComponent, PetriNetTab tab, PetriNetView view)
    {
        _viewComponent = viewComponent;
        _view = tab;
        _model = view;
        _viewComponent.markAsDeleted();
    }


    /** */
    public void redo()
    {
        _viewComponent.delete();
    }


    /** */
    public void undo()
    {
        _viewComponent.undelete(_model, _view);
    }


    public String toString()
    {
        return super.toString() + " " + _viewComponent.getClass().getSimpleName()
                + " [" + _viewComponent.getId() + "]";
    }

}
