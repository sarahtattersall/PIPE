package pipe.reachability.algorithm;

import uk.ac.imperial.state.ClassifiedState;

/**
 * Record containing a  state and the rate into it.
 * USed for the state space exploration algorithm to perform on the fly
 * elimination of vanishing states.
 */
public class StateRateRecord {

    /**
     * State
     */
    private final ClassifiedState state;

    /**
     * Rate into the state
     */
    private double rate;

    public StateRateRecord(ClassifiedState state, double rate) {
        this.state = state;
        this.rate = rate;
    }

    /**
     *
     * @return rate into the vanishing state
     */
    public double getRate() {
        return rate;
    }

    /**
     *
     * @return vanishing state rate
     */
    public ClassifiedState getState() {
        return state;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
