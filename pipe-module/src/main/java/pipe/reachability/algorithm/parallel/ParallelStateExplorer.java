package pipe.reachability.algorithm.parallel;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.StateRateRecord;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


/**
 * Callable worker that is given a state to explore and calculates the successors
 * of the state.
 *
 */
public class ParallelStateExplorer implements Callable<Map<ClassifiedState, Double>> {

    /**
     * Count down latch, this value is decremented once the call method
     * has finished processing
     */
    private final CountDownLatch latch;

    /**
     * State to explore successors of in call method
     */
    private final ClassifiedState state;

    /**
     * Utilities associated with the Petri net that the state belongs to.
     * They provide a means to query for the states successors
     */
    private final ExplorerUtilities explorerUtilities;

    /**
     * Describes how to explore vanishing states
     */
    private final VanishingExplorer vanishingExplorer;

    public ParallelStateExplorer(CountDownLatch latch, ClassifiedState state, ExplorerUtilities explorerUtilities,
                                 VanishingExplorer vanishingExplorer) {

        this.latch = latch;
        this.state = state;
        this.explorerUtilities = explorerUtilities;
        this.vanishingExplorer = vanishingExplorer;
    }

    /**
     * Performs state space exploration of the given state
     *
     * @return successors
     */
    @Override
    public Map<ClassifiedState, Double> call() throws TimelessTrapException {
        try {
            Map<ClassifiedState, Double> stateRates = new HashMap<>();
            for (ClassifiedState successor : explorerUtilities.getSuccessors(state)) {
                double rate = explorerUtilities.rate(state, successor);
                if (successor.isTangible()) {
                    registerStateRate(successor, rate, stateRates);
                } else {
                    Collection<StateRateRecord> explorableStates = vanishingExplorer.explore(successor, rate);
                    for (StateRateRecord record : explorableStates) {
                        registerStateRate(record.getState(), record.getRate(), stateRates);
                    }
                }
            }
            return stateRates;
        } finally {
            latch.countDown();
        }
    }

    private void registerStateRate(ClassifiedState successor, double rate, Map<ClassifiedState, Double> stateRates) {
        if (stateRates.containsKey(successor)) {
            double previousRate = stateRates.get(successor);
            stateRates.put(successor, previousRate + rate);
        } else {
            stateRates.put(successor, rate);
        }
    }
}
