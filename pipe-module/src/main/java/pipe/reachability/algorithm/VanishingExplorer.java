package pipe.reachability.algorithm;

import uk.ac.imperial.state.ClassifiedState;

import java.util.Collection;

/**
 * Interface used to explore all vanishing states.
 *
 * Further implementations can choose to eliminate them from the steady state exploration
 * or incorporate them into the exploration
 */
public interface VanishingExplorer {

    /**
     *
     * @param vanishingState vanishing state to explore.
     * @param rate rate at which vanishingState is entered from the previous state
     * @return Collection of states found to explore whilst processing the vanishing state
     * @throws TimelessTrapException
     */
    Collection<StateRateRecord> explore(ClassifiedState vanishingState, double rate) throws TimelessTrapException;
}
