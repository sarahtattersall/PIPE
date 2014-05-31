/*
 * PetriNetObjectNameEdit.java
 */
package pipe.historyActions.component;


import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author corveau
 */
public class ChangePetriNetComponentName extends AbstractUndoableEdit {

    private final String oldName;

    private final String newName;

    private final PetriNetComponent component;


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

    /** */
    @Override
    public void undo() {
        super.undo();
        component.setId(oldName);
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        component.setId(newName);
    }

}
