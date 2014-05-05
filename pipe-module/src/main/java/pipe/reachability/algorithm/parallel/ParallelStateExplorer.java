package pipe.reachability.algorithm.parallel;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.StateRateRecord;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.state.State;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


public class ParallelStateExplorer implements Callable<Map<State, Double>> {

    /**
     * Count down latch, this value is decremented once the call method
     * has finished processing
     */
    private final CountDownLatch latch;

    /**
     * State to explore successors of in call method
     */
    private final State state;

    private final ExplorerUtilities explorerUtilities;

    private final VanishingExplorer vanishingExplorer;

    public ParallelStateExplorer(CountDownLatch latch, State state, ExplorerUtilities explorerUtilities,
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
    public Map<State, Double> call() throws TimelessTrapException {
        Map<State, Double> stateRates = new HashMap<>();
        for (State successor : explorerUtilities.getSuccessors(state)) {
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
        latch.countDown();
        return stateRates;
    }

    private void registerStateRate(State successor, double rate, Map<State, Double> stateRates) {
        if (stateRates.containsKey(successor)) {
            double previousRate = stateRates.get(successor);
            stateRates.put(successor, previousRate + rate);
        } else {
            stateRates.put(successor, rate);
        }
    }
}
