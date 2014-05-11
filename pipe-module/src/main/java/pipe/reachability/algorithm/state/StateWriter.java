package pipe.reachability.algorithm.state;

import pipe.reachability.state.ExplorerState;

import java.io.IOException;

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
    void transition(ExplorerState previous, ExplorerState state, double rate);

    /**
     * Clear any cached states that have been saved speeding up the writing process
     */
    void clear() throws IOException;
}
