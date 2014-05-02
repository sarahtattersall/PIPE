package pipe.reachability.algorithm;

import pipe.reachability.state.State;

/**
 * Record containing a  state and the rate into it.
 * USed for the Reachability algorithm to perform on the fly
 * elimination of vanishing states.
 */
public class StateRateRecord {

    /**
     * State
     */
    private final State state;

    /**
     * Rate into the state
     */
    private double rate;

    public StateRateRecord(State state, double rate) {
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
    public State getState() {
        return state;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
