/*
 * SetRateParameterEdit.java
 */
package pipe.historyActions;


import pipe.models.component.rate.Rate;
import pipe.models.component.transition.Transition;


/**
 * @author corveau
 */
public class SetRateParameter extends HistoryItem {

    /**
     * Transition to change rate of
     */
    private final Transition transition;

    /**
     * Previous value of the rate
     */
    private final Rate oldRate;

    /**
     * New rate value
     */
    private final Rate newRate;

    public SetRateParameter(Transition transition, Rate oldRate, Rate newRate) {
        this.transition = transition;
        this.oldRate = oldRate;
        this.newRate = newRate;
    }

    /** */
    @Override
    public void undo() {
        transition.setRate(oldRate);
    }

    /** */
    @Override
    public void redo() {
        transition.setRate(newRate);
    }

}
