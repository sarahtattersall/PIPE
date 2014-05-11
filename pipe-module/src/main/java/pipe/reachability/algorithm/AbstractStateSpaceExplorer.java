package pipe.reachability.algorithm;

import pipe.reachability.algorithm.state.StateSpaceExplorer;
import uk.ac.imperial.io.StateProcessor;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.State;
import uk.ac.imperial.utils.ExploredSet;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractStateSpaceExplorer implements StateSpaceExplorer {
    /**
     * Used for processing transitions
     */
    protected final StateProcessor stateProcessor;

    /**
     * Used for exploring vanishing states
     */
    protected final VanishingExplorer vanishingExplorer;

    /**
     * Map to register successor states to their rate when exploring a state.
     * <p/>
     * When processing a tangible state it is possible that via multiple vanishing states
     * the same tangible state is the successor. In this case the rates must be summed.
     * <p/>
     * This map is therefore used to write transitions to temporarily whilst processing
     * all successors of a state. It is then used to write the records to the stateWriter
     * only once all successors have been processed.
     */
    protected final Map<ClassifiedState, Double> successorRates = new HashMap<>();


    /**
     * Performs useful state calculations
     */
    protected ExplorerUtilities explorerUtilities;

    /**
     * Queue for states yet to be explored
     */
    protected Queue<ClassifiedState> explorationQueue = new ArrayDeque<>();

    /**
     * Contains states that have already been explored.
     * Initialised in generate when initialState info is given
     */
    protected ExploredSet explored;

    /**
     * Number of states that have been processed during exploraton
     */
    public int processedCount = 0;

    public AbstractStateSpaceExplorer(ExplorerUtilities explorerUtilities, VanishingExplorer vanishingExplorer,
                                      StateProcessor stateProcessor) {
        this.explorerUtilities = explorerUtilities;
        this.vanishingExplorer = vanishingExplorer;
        this.stateProcessor = stateProcessor;
    }

    @Override
    public int generate(ClassifiedState initialState)
            throws TimelessTrapException, InterruptedException, ExecutionException, IOException {
        initialiseExplored(initialState);
        exploreInitialState(initialState);
        stateSpaceExploration();
        return processedCount;

    }

    private void initialiseExplored(State state) {
        List<String> placeOrder = getPlaceNames(state);
        explored = new ExploredSet(300_000, placeOrder);
    }

    /**
     *
     * @param state
     * @return List of place names contained in state
     */
    private List<String> getPlaceNames(State state) {
        return new LinkedList<>(state.getPlaces());
    }

    /**
     * Populates tangibleQueue with all starting tangible states.
     * <p/>
     * In the case that initialState is tangible then this is just
     * added to the queue.
     * <p/>
     * Otherwise it must sort through vanishing states
     *
     * @param initialState starting state of the algorithm
     */
    protected void exploreInitialState(ClassifiedState initialState) throws TimelessTrapException {
        if (initialState.isTangible()) {
            explorationQueue.add(initialState);
            markAsExplored(initialState);
        } else {
            Collection<StateRateRecord> explorableStates = vanishingExplorer.explore(initialState, 1.0);
            for (StateRateRecord record : explorableStates) {
                registerStateTransition(record.getState(), record.getRate());
            }
        }

    }

    protected abstract void stateSpaceExploration()
            throws InterruptedException, ExecutionException, TimelessTrapException, IOException;

    /**
     * Adds a compressed version of a tangible state to exploredStates
     *
     * @param state state that has been explored
     */
    //TODO: IMPLEMENT COMPRESSED VERSION
    protected void markAsExplored(ClassifiedState state) {
        explored.add(state);
    }

    /**
     * registers a transition to the successor in stateRecords and
     * adds the successor to the exploredQueue if it is not already contained in it.
     *  @param successor state that is possible via an enabled transition from state
     * @param rate      rate at which state transitions to successor
     */
    protected void registerStateTransition(ClassifiedState successor, double rate) {
        registerStateRate(successor, rate);
        if (!explored.contains(successor)) {
            explorationQueue.add(successor);
            markAsExplored(successor);
        }
    }

    /**
     * Register the successor into successorRates map
     * <p/>
     * If successor already exists then the rate is summed, if not it
     * is added as a new entry
     *
     * @param successor key to successor rates
     * @param rate      rate at which successor is entered via some transition
     */
    protected void registerStateRate(ClassifiedState successor, double rate) {
        if (successorRates.containsKey(successor)) {
            double previousRate = successorRates.get(successor);
            successorRates.put(successor, previousRate + rate);
        } else {
            successorRates.put(successor, rate);
        }
    }

    /**
     * This method writes all state transitions in the map stateTransitions out to the
     * state writer.
     * <p/>
     * It is assumed that no duplicate transitions exist by this point and that their rates
     * have been summed up. If this is not the case then multiple transitions will be written to
     * disk and must be dealt with accordingly.
     *
     * @param state the current state that successors belong to
     * @param successorRates
     */
    protected void writeStateTransitions(ClassifiedState state, Map<ClassifiedState, Double> successorRates) {
        stateProcessor.processTransitions(state, successorRates);
        processedCount += successorRates.size();
    }
}
