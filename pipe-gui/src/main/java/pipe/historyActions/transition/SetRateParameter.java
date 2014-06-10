/*
 * SetRateParameterEdit.java
 */
package pipe.historyActions.transition;


import uk.ac.imperial.pipe.models.petrinet.Rate;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.undo.AbstractUndoableEdit;


/**
 * Changes the rate parameter for a transition accordingly
 */
public class SetRateParameter extends AbstractUndoableEdit {

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

    /**
     * Constructor
     * @param transition underlying transition model
     * @param oldRate old transition rate
     * @param newRate new transition rate
     */
    public SetRateParameter(Transition transition, Rate oldRate, Rate newRate) {
        this.transition = transition;
        this.oldRate = oldRate;
        this.newRate = newRate;
    }

    /**
     * Set the transition rate to the old rate
     */
    @Override
    public void undo() {
        super.undo();
        transition.setRate(oldRate);
    }

    /**
     * Set hte transition rate to the new rate
     */
    @Override
    public void redo() {
        super.redo();
        transition.setRate(newRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetRateParameter)) {
            return false;
        }

        SetRateParameter that = (SetRateParameter) o;

        if (!newRate.equals(that.newRate)) {
            return false;
        }
        if (!oldRate.equals(that.oldRate)) {
            return false;
        }
        if (!transition.equals(that.transition)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = transition.hashCode();
        result = 31 * result + oldRate.hashCode();
        result = 31 * result + newRate.hashCode();
        return result;
    }
}
