package pipe.reachability.algorithm.state;

import pipe.reachability.algorithm.StateRateRecord;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Arrays;
import java.util.Collection;

/**
 * This state performs no computation of vanishing states and simply
 * returns them to the user to be explored normally
 */
public class SimpleVanishingExplorer implements VanishingExplorer {
    @Override
    public Collection<StateRateRecord> explore(ClassifiedState vanishingState, double rate) throws TimelessTrapException {
        return Arrays.asList(new StateRateRecord(vanishingState, rate));
    }
}
