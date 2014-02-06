/*
 * RateParameterValueEdit.java
 */

package pipe.historyActions;


import pipe.models.component.rate.RateParameter;

/**
 * HistoryItem responsible for undo/redoing a rate parameters
 * expression
 */
public class RateParameterValue extends HistoryItem {

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

    public RateParameterValue(RateParameter rateParameter, String previousExpression, String newExpression) {
        this.rateParameter = rateParameter;
        this.previousExpression = previousExpression;
        this.newExpression = newExpression;
    }

    @Override
    public void undo() {
        rateParameter.setExpression(previousExpression);
    }

    /** */
    @Override
    public void redo() {
        rateParameter.setExpression(newExpression);
    }

}
