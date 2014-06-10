/*
 * PetriNetObjectNameEdit.java
 */
package pipe.historyActions.component;


import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undo/Redo action for changing a Petri net components name/id
 */
public final class ChangePetriNetComponentName extends AbstractUndoableEdit {

    /**
     * Old name
     */
    private final String oldName;

    /**
     * name name
     */
    private final String newName;

    /**
     * Petri net component
     */
    private final PetriNetComponent component;


    /**
     *
     * @param component component whose name will change
     * @param oldName old component name
     * @param newName new component name
     */
    public ChangePetriNetComponentName(PetriNetComponent component, String oldName, String newName) {
        this.component = component;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public int hashCode() {
        int result = oldName.hashCode();
        result = 31 * result + newName.hashCode();
        result = 31 * result + component.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangePetriNetComponentName)) {
            return false;
        }

        ChangePetriNetComponentName that = (ChangePetriNetComponentName) o;

        if (!component.equals(that.component)) {
            return false;
        }
        if (!newName.equals(that.newName)) {
            return false;
        }
        if (!oldName.equals(that.oldName)) {
            return false;
        }

        return true;
    }

    /**
     *  Sets the component id to the old name
     */
    @Override
    public void undo() {
        super.undo();
        component.setId(oldName);
    }


    /**
     * Sets the component id to the new name
     */
    @Override
    public void redo() {
        super.redo();
        component.setId(newName);
    }

}
