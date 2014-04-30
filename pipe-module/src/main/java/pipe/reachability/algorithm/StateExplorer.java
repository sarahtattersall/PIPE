package pipe.reachability.algorithm;

import pipe.reachability.state.State;

/**
 * This class is used in conjunction with the {@link pipe.reachability.algorithm.StateSpaceExplorer}
 * and should provide the code for exploring each different type of state.
 */
public interface StateExplorer {

    /**
     * Explores the current state, processing it as necessary
     *
     * Arguments correspond to a transition from previous to state with the given rate
     *
     * @param previous previous state
     * @param state state to explore
     * @param rate  rate at which the state is entered
     */
    void explore(State previous, State state, double rate);
}
