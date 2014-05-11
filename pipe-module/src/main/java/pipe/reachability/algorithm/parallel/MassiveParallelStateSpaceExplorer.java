package pipe.reachability.algorithm.parallel;

import pipe.reachability.algorithm.*;
import uk.ac.imperial.io.StateProcessor;
import uk.ac.imperial.state.ClassifiedState;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MassiveParallelStateSpaceExplorer extends AbstractStateSpaceExplorer {
    /**
     * Number of states to analyse sequentially per thread
     */
    private final int statesPerThread;


    protected ExecutorService executorService;


    public MassiveParallelStateSpaceExplorer(ExplorerUtilities explorerUtilities, VanishingExplorer vanishingExplorer,
                                             StateProcessor stateProcessor, int statesPerThread) {
        super(explorerUtilities, vanishingExplorer, stateProcessor);

        this.statesPerThread = statesPerThread;
    }

    /**
     * Performs state space exploration by spinning up threads and allowing them to process
     * states in parallel. The number of states that each thread processes is set in the constructor
     * and is statesPerThread.
     *
     * Results are then merged together into the explored and explorationQueue data sets
     * and transitions are written to the output stream.
     *
     * A possible extension to this is to have the threads ask for work
     * if they run out and/or dynamically scale the number of threads processed according to
     * how it benefits each different state space.
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimelessTrapException if vanishing states lead to a timeless trap
     */
    @Override
    protected void stateSpaceExploration()
            throws InterruptedException, ExecutionException, TimelessTrapException, IOException {
        executorService = Executors.newFixedThreadPool(8);
        CompletionService<Result> completionService =
                new ExecutorCompletionService<>(executorService);

        while (!explorationQueue.isEmpty()) {
            int submitted = 0;
            while (submitted < 8 && !explorationQueue.isEmpty()) {
                ClassifiedState state = explorationQueue.poll();
                completionService.submit(
                        new MultiStateExplorer(state, statesPerThread, explorerUtilities, vanishingExplorer));
                submitted++;
            }


            Set<ClassifiedState> transitions = new HashSet<>();
            Collection<ClassifiedState> unexplored = new HashSet<>();
            for (int i = 0; i < submitted; i++) {
                Result result = completionService.take().get();
                explored.addAll(result.explored);
                unexplored.addAll(result.unexplored); //TODO: Potentially adding something that is in later explored set?

                //Combine results to avoid writing dups
                for (Map.Entry<ClassifiedState, Map<ClassifiedState, Double>> entry : result.transitions.entrySet()) {
                    if (!transitions.contains(entry.getKey())) {
                        writeStateTransitions(entry.getKey(), entry.getValue());
                        transitions.add(entry.getKey());
                    }
                }
            }

            for (ClassifiedState state : unexplored) {
                if (!transitions.contains(state)) {
                    explorationQueue.add(state);
                }
            }
            explorerUtilities.clear();
        }
        executorService.shutdownNow();
    }

    /**
     * Callable implementation that explores a state and its successors up to a certain
     * depth.
     *
     * It registers all transitions that it observes
     */
    private class MultiStateExplorer implements Callable<Result> {
        /**
         * Starting state to explore
         */
        private final ClassifiedState initialState;

        /**
         * Number of states the thread is allowed to explore before finishing execution
         */
        private final int exploreCount;

        /**
         * Utilities for exploring a state within a Petri net
         */
        private final ExplorerUtilities explorerUtilities;

        /**
         * Used to explore a vanishing state
         */
        private final VanishingExplorer vanishingExplorer;

        /**
         * Transitions found whilst exploring exploreCount states
         */
        private final Map<ClassifiedState, Map<ClassifiedState, Double>> transitions = new HashMap<>();

        /**
         * States that have been explored whilst exploring exploreCount states
         */
        private final Set<ClassifiedState>  exploredStates = new HashSet<>();

        private MultiStateExplorer(ClassifiedState initialState, int exploreCount, ExplorerUtilities explorerUtilities,
                                   VanishingExplorer vanishingExplorer) {
            this.initialState = initialState;
            this.exploreCount = exploreCount;
            this.explorerUtilities = explorerUtilities;
            this.vanishingExplorer = vanishingExplorer;
        }

        /**
         * Performs sequential state space exploration using a BFS up to a certain number
         * of states
         *
         * @return the result of a BFS including any transitions seen, states that have not yet been explored
         *         and those that have.
         * @throws TimelessTrapException
         */
        @Override
        public Result call() throws TimelessTrapException {
            Queue<ClassifiedState> explorationQueue = new ArrayDeque<>();
            explorationQueue.add(initialState);
            for (int explored = 0; explored < exploreCount && !explorationQueue.isEmpty(); explored++) {
                ClassifiedState state = explorationQueue.poll();
                Map<ClassifiedState, Double> successorRates = new HashMap<>();
                for (ClassifiedState successor : explorerUtilities.getSuccessors(state)) {
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

            Set<ClassifiedState> unexplored = new HashSet<>();
            unexplored.addAll(explorationQueue);
            return new Result(transitions, unexplored, exploredStates);
        }

        /**
         *
         * @param successor
         * @param rate
         * @param successorRates
         */
        private void registerStateRate(ClassifiedState successor, double rate,
                                       Map<ClassifiedState, Double> successorRates) {
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
        private boolean seen(ClassifiedState state) {
            return exploredStates.contains(state) || explored.contains(state);
        }

        /**
         * Puts the state and its rates into the transitions data structure
         * @param state
         * @param successorRates
         */
        private void writeStateTransitions(ClassifiedState state, Map<ClassifiedState, Double> successorRates) {
            transitions.put(state, successorRates);
        }
    }


    /**
     * Basic struct that is return value of call method.
     *
     * Contains data structures to be processed on method completion.
     */
    private static class Result {
        public final Map<ClassifiedState, Map<ClassifiedState, Double>> transitions;

        public final Set<ClassifiedState> unexplored;

        public final Set<ClassifiedState> explored;

        public Result(Map<ClassifiedState, Map<ClassifiedState, Double>> transitions, Set<ClassifiedState> unexplored,
                      Set<ClassifiedState> explored) {
            this.transitions = transitions;
            this.unexplored = unexplored;
            this.explored = explored;
        }
    }
}
