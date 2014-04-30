package pipe.reachability.algorithm.sequential;

import pipe.reachability.algorithm.state.StateExplorer;
import pipe.reachability.state.State;

/**
 * This implementation of a StateExplorer does not save the vanishing state
 * to the OutputStream, only states that transition to a tangible state.
 */
public class NonSavingStateExplorer implements StateExplorer {

    @Override
    public void explore(State previous, State state, double rate) {
        // noop
    }

}
