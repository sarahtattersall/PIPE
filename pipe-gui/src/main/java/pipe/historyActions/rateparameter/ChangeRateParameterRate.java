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

    /**
     * Constructor
     * @param rateParameter underlying model
     * @param previousExpression previous expression of the rate parameter
     * @param newExpression new expression of the rate parameter
     */
    public ChangeRateParameterRate(RateParameter rateParameter, String previousExpression, String newExpression) {
        this.rateParameter = rateParameter;
        this.previousExpression = previousExpression;
        this.newExpression = newExpression;
    }

    @Override
    public int hashCode() {
        int result = rateParameter.hashCode();
        result = 31 * result + previousExpression.hashCode();
        result = 31 * result + newExpression.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangeRateParameterRate)) {
            return false;
        }

        ChangeRateParameterRate that = (ChangeRateParameterRate) o;

        if (!newExpression.equals(that.newExpression)) {
            return false;
        }
        if (!previousExpression.equals(that.previousExpression)) {
            return false;
        }
        if (!rateParameter.equals(that.rateParameter)) {
            return false;
        }

        return true;
    }

    /**
     * Sets the rate parameters expression to the previous expression
     */
    @Override
    public void undo() {
        super.undo();
        rateParameter.setExpression(previousExpression);
    }

    /**
     * Sets the rate parameters expression to the new expression
     */
    @Override
    public void redo() {
        super.redo();
        rateParameter.setExpression(newExpression);
    }

}
