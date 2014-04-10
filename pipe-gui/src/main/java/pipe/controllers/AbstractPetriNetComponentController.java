package pipe.controllers;

import pipe.historyActions.PetriNetObjectName;
import pipe.models.component.PetriNetComponent;

public abstract class AbstractPetriNetComponentController<T extends PetriNetComponent> {
    protected final T component;

    protected AbstractPetriNetComponentController(final T component) {
        this.component = component;
    }

    public void setName(String newName) {
        String oldName = component.getId();
        PetriNetObjectName nameAction = new PetriNetObjectName(component, oldName, newName);
        nameAction.redo();
        //TODO: HISTORY OF SETTING NAME?
        //        historyManager.addNewEdit(nameAction);
    }
}
