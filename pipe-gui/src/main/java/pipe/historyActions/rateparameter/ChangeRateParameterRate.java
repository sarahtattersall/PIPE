/*
 * RateParameterValueEdit.java
 */

package pipe.historyActions.rateparameter;


import uk.ac.imperial.pipe.models.petrinet.RateParameter;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undoable edit responsible for undo/redoing a rate parameters
 * expression
 */
public class ChangeRateParameterRate extends AbstractUndoableEdit {

    /**
     * Rate parameter whose expression has changed
     */
    private final RateParameter rateParameter;

    /**
     * Previous value
     */
    private final String previousExpression;

    /**
     * Value the expression has been changed to
     */
    private final String newExpression;

    public ChangeRateParameterRate(RateParameter rateParameter, String previousExpression, String newExpression) {
        this.rateParameter = rateParameter;
        this.previousExpression = previousExpression;
        this.newExpression = newExpression;
    }

    @Override
    public void undo() {
        super.undo();
        rateParameter.setExpression(previousExpression);
    }

    /** */
    @Override
    public void redo() {
        super.redo();
        rateParameter.setExpression(newExpression);
    }

}
