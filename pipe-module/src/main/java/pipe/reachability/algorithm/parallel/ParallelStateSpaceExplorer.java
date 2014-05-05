package pipe.reachability.algorithm.parallel;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.state.ExplorerState;

import java.util.*;
import java.util.concurrent.*;

public class ParallelStateSpaceExplorer implements StateSpaceExplorer {
    private final ExecutorService executorService;

    /**
     * Used for writing transitions
     */
    private final StateWriter stateWriter;

    /**
     * Used for exploring vanishing states
     */
    private final VanishingExplorer vanishingExplorer;

    /**
     * Performs useful state calculations
     */
    private final ExplorerUtilities explorerUtilities;

    /**
     * Queue for states yet to be explored
     */
    Queue<ExplorerState> explorationQueue = new ArrayDeque<>();

    /**
     * Contains states that have already been explored.
     */
    private Set<ExplorerState> explored = new HashSet<>();

    public ParallelStateSpaceExplorer(StateWriter stateWriter, VanishingExplorer vanishingExplorer,
                                      ExplorerUtilities explorerUtilities) {
        this.stateWriter = stateWriter;
        this.vanishingExplorer = vanishingExplorer;
        this.explorerUtilities = explorerUtilities;
        executorService = Executors.newFixedThreadPool(8);

    }

    @Override
    public void generate() throws TimelessTrapException, InterruptedException, ExecutionException {
        stateSpaceExploration();

    }

    private void stateSpaceExploration() throws InterruptedException, ExecutionException {
        int elemsAtCurrentLevel = explorationQueue.size();
        int elemsAtNextLevel = 0;
        while (!explorationQueue.isEmpty()) {
            Map<ExplorerState, Future<Map<ExplorerState, Double>>> successorFutures = new HashMap<>();
            CountDownLatch latch = new CountDownLatch(elemsAtCurrentLevel);
            for (int i = 0; i < elemsAtCurrentLevel; i++) {
                ExplorerState state = explorationQueue.poll();
                successorFutures.put(state, executorService.submit(
                        new ParallelStateExplorer(latch, state, explorerUtilities, vanishingExplorer)));
            }

            latch.await();
            for (Map.Entry<ExplorerState, Future<Map<ExplorerState, Double>>> entry : successorFutures.entrySet()) {
                Future<Map<ExplorerState, Double>> future = entry.getValue();
                ExplorerState state = entry.getKey();
                for (Map.Entry<ExplorerState, Double> successorEntry : future.get().entrySet()) {
                    ExplorerState successor = successorEntry.getKey();
                    double rate = successorEntry.getValue();
                    stateWriter.transition(state, successor, rate);
                    if (!explorationQueue.contains(successor)) {
                        elemsAtNextLevel++;
                        explorationQueue.add(successor);
                    }
                }
            }
            elemsAtCurrentLevel = elemsAtNextLevel;
            elemsAtNextLevel = 0;

        }
    }
}
