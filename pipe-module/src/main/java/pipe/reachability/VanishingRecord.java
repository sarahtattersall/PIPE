package pipe.reachability;

import java.util.Map;

/**
 * Wraps {@link pipe.reachability.HashedState} providing additional
 * functionality to provide vanishing state rates
 */
public class VanishingRecord {

    private final State state;

    private final double rate;

    public VanishingRecord(Map<String, Integer> tokens, double rate) {
        this(new HashedState(tokens), rate);
    }

    public VanishingRecord(State state, double rate) {
        this.state = state;
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public State getState() {
        return state;
    }
}
