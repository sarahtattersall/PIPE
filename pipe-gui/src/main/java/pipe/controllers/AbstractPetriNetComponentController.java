package pipe.controllers;

import pipe.historyActions.HistoryManager;
import pipe.historyActions.PetriNetObjectName;
import pipe.models.component.PetriNetComponent;

public abstract class AbstractPetriNetComponentController<T extends PetriNetComponent> {
    protected final T component;
    protected final HistoryManager historyManager;

    protected AbstractPetriNetComponentController(final T component,
                                                  final HistoryManager historyManager) {
        this.component = component;
        this.historyManager = historyManager;
    }

    public void setName(String newName) {
        String oldName = component.getId();
        PetriNetObjectName nameAction = new PetriNetObjectName(component, oldName, newName);
        nameAction.redo();
        historyManager.addNewEdit(nameAction);
    }
}
