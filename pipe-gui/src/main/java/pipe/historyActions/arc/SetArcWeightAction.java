/*
 * ArcWeightEdit.java
 */

package pipe.historyActions.arc;


import uk.ac.imperial.pipe.models.component.Connectable;
import uk.ac.imperial.pipe.models.component.arc.Arc;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undable edit for setting the arc weight for a token id
 */
public class SetArcWeightAction<S extends Connectable, T extends Connectable> extends AbstractUndoableEdit {

    private final Arc<S,T> arc;
    private final String token;
    private final String newWeight;
    private final String oldWeight;

    public SetArcWeightAction(Arc<S, T> arc, String token, String oldWeight, String newWeight) {

        this.arc = arc;
        this.token = token;
        this.oldWeight = oldWeight;
        this.newWeight = newWeight;
    }

    @Override
    public void undo() {
        super.undo();
        arc.setWeight(token, oldWeight);
    }

    /** */
    @Override
    public void redo() {
        super.redo();
        arc.setWeight(token, newWeight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SetArcWeightAction setArcWeightAction = (SetArcWeightAction) o;

        if (!arc.equals(setArcWeightAction.arc)) return false;
        if (!newWeight.equals(setArcWeightAction.newWeight)) return false;
        if (!oldWeight.equals(setArcWeightAction.oldWeight)) return false;
        if (!token.equals(setArcWeightAction.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arc.hashCode();
        result = 31 * result + token.hashCode();
        result = 31 * result + newWeight.hashCode();
        result = 31 * result + oldWeight.hashCode();
        return result;
    }
}
