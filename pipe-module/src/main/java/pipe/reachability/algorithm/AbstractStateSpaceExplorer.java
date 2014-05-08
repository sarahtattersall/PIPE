package pipe.reachability.algorithm;

import pipe.reachability.algorithm.state.StateSpaceExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.state.ExploredSet;
import pipe.reachability.state.ExplorerState;

import java.util.*;
import java.util.concurrent.ExecutionException;

public abstract class AbstractStateSpaceExplorer implements StateSpaceExplorer {
    /**
     * Used for writing transitions
     */
    protected final StateWriter stateWriter;

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
    protected final Map<ExplorerState, Double> successorRates = new HashMap<>();


    /**
     * Performs useful state calculations
     */
    protected ExplorerUtilities explorerUtilities;

    /**
     * Queue for states yet to be explored
     */
    protected Queue<ExplorerState> explorationQueue = new ArrayDeque<>();

    /**
     * Contains states that have already been explored.
     */
    protected ExploredSet explored = new ExploredSet();

    /**
     * Number of states that have been written to the writer
     */
    protected int writtenCount = 0;

    public AbstractStateSpaceExplorer(ExplorerUtilities explorerUtilities, VanishingExplorer vanishingExplorer,
                                      StateWriter stateWriter) {
        this.explorerUtilities = explorerUtilities;
        this.vanishingExplorer = vanishingExplorer;
        this.stateWriter = stateWriter;
    }

    @Override
    public void generate(ExplorerState initialState)
            throws TimelessTrapException, InterruptedException, ExecutionException {

        exploreInitialState(initialState);
        stateSpaceExploration();
        System.out.println("WRote " + writtenCount + " Transitions");

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
    protected void exploreInitialState(ExplorerState initialState) throws TimelessTrapException {
        if (initialState.isTangible()) {
            explorationQueue.add(initialState);
            markAsExplored(initialState);
        } else {
            Collection<StateRateRecord> explorableStates = vanishingExplorer.explore(initialState, 1.0);
            for (StateRateRecord record : explorableStates) {
                registerStateTransition(null, record.getState(), record.getRate());
            }
        }

    }

    protected abstract void stateSpaceExploration() throws InterruptedException, ExecutionException, TimelessTrapException;

    /**
     * Adds a compressed version of a tangible state to exploredStates
     *
     * @param state state that has been explored
     */
    //TODO: IMPLEMENT COMPRESSED VERSION
    protected void markAsExplored(ExplorerState state) {
        explored.add(state);
    }

    /**
     * registers a transition to the successor in stateRecords and
     * adds the successor to the exploredQueue if it is not already contained in it.
     *
     * @param state     current state
     * @param successor state that is possible via an enabled transition from state
     * @param rate      rate at which state transitions to successor
     */
    protected void registerStateTransition(ExplorerState state, ExplorerState successor, double rate) {
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
    protected void registerStateRate(ExplorerState successor, double rate) {
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
    protected void writeStateTransitions(ExplorerState state, Map<ExplorerState, Double> successorRates) {
        for (Map.Entry<ExplorerState, Double> entry : successorRates.entrySet()) {
            ExplorerState successor = entry.getKey();
            double rate = entry.getValue();
            stateWriter.transition(state, successor, rate);
            writtenCount++;
        }
    }
}
