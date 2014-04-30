package pipe.reachability.algorithm.state;

import pipe.reachability.state.State;

/**
 * This class is used in conjunction with the {@link StateSpaceExplorer}
 * and should provides the implementation for registering state transitions
 */
public interface StateWriter {

    /**
     * Write the current state transition from previous to state with the given rate
     *
     * @param previous previous state
     * @param state state to explore
     * @param rate  rate at which the state is entered
     */
    void transition(State previous, State state, double rate);
}
