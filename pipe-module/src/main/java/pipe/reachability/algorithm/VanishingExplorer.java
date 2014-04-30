package pipe.reachability.algorithm;

import pipe.reachability.state.State;

import java.util.Collection;

/**
 * Interface used to explore all vanishing states and eliminate them from the
 * steady state exploration
 */
public interface VanishingExplorer {

    /**
     *
     * @param lastTangible last known tangible state
     * @param vanishingState vanishing state to explore. It should be a successor of lastTangible
     * @param rate rate at which vanishingState is entered from lastTangible
     * @return Collection of tangible states found whilst exploring
     * @throws TimelessTrapException
     */
    Collection<State> explore(State lastTangible, State vanishingState, double rate) throws TimelessTrapException;
}
