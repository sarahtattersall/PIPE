package pipe.reachability.algorithm.sequential;

import pipe.reachability.algorithm.*;
import uk.ac.imperial.io.StateProcessor;
import uk.ac.imperial.state.ClassifiedState;

import java.io.IOException;
import java.util.Collection;

/**
 * This class performs state space exploration sequentially to determine the reachability of each state
 * Vanishing states can be explored in numerous ways so a {@link pipe.reachability.algorithm.VanishingExplorer}
 * is used to determine how to process them.
 */
public class SequentialStateSpaceExplorer extends AbstractStateSpaceExplorer {


    public SequentialStateSpaceExplorer(ExplorerUtilities explorerUtilities, VanishingExplorer vanishingExplorer,
                                        StateProcessor stateProcessor) {
        super(explorerUtilities, vanishingExplorer, stateProcessor);
    }

    /**
     * Performs state space exploration of the tangibleQueue
     * popping a state off the stack and exploring all its successors.
     * <p/>
     * It records the reachability graph into the writer
     *
     */
    @Override
    protected void stateSpaceExploration() throws TimelessTrapException, IOException {
        while (!explorationQueue.isEmpty()) {
            ClassifiedState state = explorationQueue.poll();
            successorRates.clear();
            for (ClassifiedState successor : explorerUtilities.getSuccessors(state)) {
                double rate = explorerUtilities.rate(state, successor);
                if (successor.isTangible()) {
                    registerStateTransition(successor, rate);
                } else {
                    Collection<StateRateRecord> explorableStates = vanishingExplorer.explore(successor, rate);
                    for (StateRateRecord record : explorableStates) {
                        registerStateTransition(record.getState(), record.getRate());
                    }
                }
            }
            writeStateTransitions(state, successorRates);
            explorerUtilities.clear();
        }
    }
}
