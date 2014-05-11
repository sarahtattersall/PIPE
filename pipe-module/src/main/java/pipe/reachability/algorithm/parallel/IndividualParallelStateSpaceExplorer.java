package pipe.reachability.algorithm.parallel;

import pipe.reachability.algorithm.AbstractStateSpaceExplorer;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import uk.ac.imperial.io.StateProcessor;
import uk.ac.imperial.state.ClassifiedState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This explores individual states on seperate threads and then joins
 * their results together on the master thread.
 *
 * Yields speed ups for larger state spaces but is marginally slower for smaller ones
 */
public class IndividualParallelStateSpaceExplorer extends AbstractStateSpaceExplorer {

    public IndividualParallelStateSpaceExplorer(StateProcessor stateProcessor, VanishingExplorer vanishingExplorer,
                                                ExplorerUtilities explorerUtilities) {
        super(explorerUtilities, vanishingExplorer, stateProcessor);
        executorService = Executors.newFixedThreadPool(8);

    }
    protected ExecutorService executorService;

    @Override
    protected void stateSpaceExploration() throws InterruptedException, ExecutionException, TimelessTrapException {
        if (executorService.isTerminated()) {
            executorService = Executors.newFixedThreadPool(8);
        }
        int elemsAtCurrentLevel = explorationQueue.size();
        int elemsAtNextLevel = 0;
        while (!explorationQueue.isEmpty()) {

            Map<ClassifiedState, Future<Map<ClassifiedState, Double>>> successorFutures = new HashMap<>();
            CountDownLatch latch = new CountDownLatch(elemsAtCurrentLevel);
            for (int i = 0; i < elemsAtCurrentLevel; i++) {
                ClassifiedState state = explorationQueue.poll();
                successorFutures.put(state, executorService.submit(
                        new ParallelStateExplorer(latch, state, explorerUtilities, vanishingExplorer)));
            }

            latch.await();
            for (Map.Entry<ClassifiedState, Future<Map<ClassifiedState, Double>>> entry : successorFutures.entrySet()) {
                Future<Map<ClassifiedState, Double>> future = entry.getValue();
                ClassifiedState state = entry.getKey();
                successorRates.clear();

                try {
                    Map<ClassifiedState, Double> successors = future.get();
                    for (Map.Entry<ClassifiedState, Double> successorEntry : successors.entrySet()) {
                        ClassifiedState successor = successorEntry.getKey();
                        double rate = successorEntry.getValue();
                        registerStateRate(successor, rate);
                        if (!explored.contains(successor)) {
                            elemsAtNextLevel++;
                            explorationQueue.add(successor);
                            markAsExplored(successor);
                        }
                    }
                } catch (ExecutionException ee) {
                    throw new TimelessTrapException();
                }
                writeStateTransitions(state, successorRates);
            }
            elemsAtCurrentLevel = elemsAtNextLevel;
            elemsAtNextLevel = 0;

        }
        executorService.shutdownNow();

    }

}
