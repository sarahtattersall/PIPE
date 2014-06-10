/*
 * ArcWeightEdit.java
 */

package pipe.historyActions.arc;


import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Arc;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undable edit for setting the arc weight for a token id
 */
public class SetArcWeightAction<S extends Connectable, T extends Connectable> extends AbstractUndoableEdit {

    /**
     * Arc model
     */
    private final Arc<S,T> arc;

    /**
     * Token id for the weight
     */
    private final String token;

    /**
     * New functional weight for the specified token id
     */
    private final String newWeight;

    /**
     * Old functional weight for the specfied token id
     */
    private final String oldWeight;

    /**
     *
     * @param arc model whose weight has changed
     * @param token token id for the weight of the arc
     * @param oldWeight old arc functional weight for the token id
     * @param newWeight new arc functional weight for the token id
     */
    public SetArcWeightAction(Arc<S, T> arc, String token, String oldWeight, String newWeight) {

        this.arc = arc;
        this.token = token;
        this.oldWeight = oldWeight;
        this.newWeight = newWeight;
    }

    /**
     * Sets the arc's token id weight to the old functional weight
     */
    @Override
    public void undo() {
        super.undo();
        arc.setWeight(token, oldWeight);
    }

    /**
     * Sets the arc's token id weight to the new functional weight
     */
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
