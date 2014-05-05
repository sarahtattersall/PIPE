package pipe.reachability.algorithm;

import pipe.reachability.state.State;

/**
 * Record containing a vanishing state and the rate into it.
 * USed for the Reachability algorithm to perform on the fly
 * elimination of vanishing states.
 */
public class VanishingRecord {

    /**
     * Vanishing state
     */
    private final State state;

    /**
     * Rate into the state
     */
    private final double rate;

    public VanishingRecord(State state, double rate) {
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
}
