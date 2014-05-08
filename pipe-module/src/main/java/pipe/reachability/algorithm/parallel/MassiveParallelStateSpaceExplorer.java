package pipe.reachability.algorithm.parallel;

import pipe.reachability.algorithm.*;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.state.ExplorerState;

import java.util.*;
import java.util.concurrent.*;

public class MassiveParallelStateSpaceExplorer extends AbstractStateSpaceExplorer {
    /**
     * Number of states to analyse sequentially per thread
     */
    private final int statesPerThread;

    public MassiveParallelStateSpaceExplorer(ExplorerUtilities explorerUtilities, VanishingExplorer vanishingExplorer,
                                             StateWriter stateWriter, int statesPerThread) {
        super(explorerUtilities, vanishingExplorer, stateWriter);

        this.statesPerThread = statesPerThread;
    }

    @Override
    protected void stateSpaceExploration() throws InterruptedException, ExecutionException, TimelessTrapException {
        executorService = Executors.newFixedThreadPool(8);
        CompletionService<MultiStateExplorer.Result> completionService =
                new ExecutorCompletionService<>(executorService);

        while (!explorationQueue.isEmpty()) {
            int submitted = 0;
            while (submitted < 8 && !explorationQueue.isEmpty()) {
                ExplorerState state = explorationQueue.poll();
                //                if (!explored.contains(state)) { //TODO: Some duplicates possible/ already explored?
                completionService.submit(
                        new MultiStateExplorer(state, explored, statesPerThread, explorerUtilities, vanishingExplorer));
                submitted++;
                //                }
            }

            for (int i = 0; i < submitted; i++) {
                MultiStateExplorer.Result result = completionService.take().get();
                explored.addAll(result.explored);
                explorationQueue.addAll(result.unexplored);
                for (Map.Entry<ExplorerState, Map<ExplorerState, Double>> entry : result.transitions.entrySet()) {
                    writeStateTransitions(entry.getKey(), entry.getValue());
                }
            }
        }
        executorService.shutdownNow();
    }

    private static class MultiStateExplorer implements Callable<MultiStateExplorer.Result> {

        private final ExplorerState initialState;

        private final int exploreCount;

        private final ExplorerUtilities explorerUtilities;

        private final VanishingExplorer vanishingExplorer;

        private final Map<ExplorerState, Map<ExplorerState, Double>> transitions = new HashMap<>();

        private final Set<ExplorerState> exploredStates = new HashSet<>();

        private final Set<ExplorerState> previouslySeen;

        private MultiStateExplorer(ExplorerState initialState, Set<ExplorerState> previouslySeen, int exploreCount,
                                   ExplorerUtilities explorerUtilities, VanishingExplorer vanishingExplorer) {
            this.initialState = initialState;
            this.exploreCount = exploreCount;
            this.explorerUtilities = explorerUtilities;
            this.vanishingExplorer = vanishingExplorer;
            this.previouslySeen = previouslySeen;
        }

        @Override
        public Result call() throws TimelessTrapException {
            Queue<ExplorerState> explorationQueue = new ArrayDeque<>();
            explorationQueue.add(initialState);
            for (int explored = 0; explored < exploreCount && !explorationQueue.isEmpty(); explored++) {
                ExplorerState state = explorationQueue.poll();
                Map<ExplorerState, Double> successorRates = new HashMap<>();
                for (ExplorerState successor : explorerUtilities.getSuccessors(state)) {
                    double rate = explorerUtilities.rate(state, successor);
                    if (successor.isTangible()) {
                        registerStateRate(successor, rate, successorRates);
                        if (!seen(successor)) {
                            explorationQueue.add(successor);
                            exploredStates.add(successor);
                        }
                    } else {
                        Collection<StateRateRecord> explorableStates = vanishingExplorer.explore(successor, rate);
                        for (StateRateRecord record : explorableStates) {
                            registerStateRate(record.getState(), rate, successorRates);
                            if (!seen(record.getState())) {
                                explorationQueue.add(record.getState());
                                exploredStates.add(record.getState());
                            }
                        }
                    }
                }
                writeStateTransitions(state, successorRates);
            }

            Set<ExplorerState> unexplored = new HashSet<>();
            unexplored.addAll(explorationQueue);
            return new Result(transitions, unexplored, exploredStates);
        }

        private void registerStateRate(ExplorerState successor, double rate,
                                       Map<ExplorerState, Double> successorRates) {
            if (successorRates.containsKey(successor)) {
                double previousRate = successorRates.get(successor);
                successorRates.put(successor, previousRate + rate);
            } else {
                successorRates.put(successor, rate);
            }
        }

        /**
         * @param state
         * @return true if the state has already been explored
         */
        private boolean seen(ExplorerState state) {
            return exploredStates.contains(state) || previouslySeen.contains(state);
        }

        private void writeStateTransitions(ExplorerState state, Map<ExplorerState, Double> successorRates) {
            transitions.put(state, successorRates);

        }

        public static class Result {
            public final Map<ExplorerState, Map<ExplorerState, Double>> transitions;

            public final Set<ExplorerState> unexplored;

            public final Set<ExplorerState> explored;

            public Result(Map<ExplorerState, Map<ExplorerState, Double>> transitions, Set<ExplorerState> unexplored,
                          Set<ExplorerState> explored) {
                this.transitions = transitions;
                this.unexplored = unexplored;
                this.explored = explored;
            }
        }


    }
}
