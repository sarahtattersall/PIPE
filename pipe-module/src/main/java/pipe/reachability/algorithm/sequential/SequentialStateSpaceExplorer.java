package pipe.reachability.algorithm.sequential;

import pipe.models.component.transition.Transition;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.StateRateRecord;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.state.State;

import java.util.*;

/**
 * This class performs state space exploration sequentially to determine the reachability of each state
 * Vanishing states can be explored in numerous ways so a {@link pipe.reachability.algorithm.VanishingExplorer}
 * is used to determine how to process them.
 */
public class SequentialStateSpaceExplorer implements StateSpaceExplorer {

    /**
     * Queue for states yet to be explored
     */
    Queue<State> explorationQueue = new ArrayDeque<>();

    /**
     * Contains states that have already been explored.
     */
    private Set<State> explored = new HashSet<>();

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
     * Map to register successor states to their rate when exploring a state.
     *
     * When processing a tangible state it is possible that via multiple vanishing states
     * the same tangible state is the successor. In this case the rates must be summed.
     *
     * This map is therefore used to write transitions to temporarily whilst processing
     * all successors of a state. It is then used to write the records to the stateWriter
     * only once all successors have been processed.
     */
    private final Map<State, Double> successorRates = new HashMap<>();


    public SequentialStateSpaceExplorer(StateWriter stateWriter, VanishingExplorer vanishingExplorer,
                                        ExplorerUtilities explorerUtilities) {
        this.stateWriter = stateWriter;
        this.vanishingExplorer = vanishingExplorer;
        this.explorerUtilities = explorerUtilities;
    }


    /**
     * Performs state space exploration
     *
     */
    @Override
    public void generate() throws TimelessTrapException {
        clearDataStructures();
        State initialState = explorerUtilities.getCurrentState();
        exploreInitialState(initialState);

        stateSpaceExploration();
    }

    /**
     * Clears any persistent data structures
     */
    private void clearDataStructures() {
        explorationQueue.clear();
        explored.clear();
    }

    /**
     * Performs state space exploration of the tangibleQueue
     * popping a state off the stack and exploring all its successors.
     * <p/>
     * It records the reachability graph into the writer
     *
     */
    private void stateSpaceExploration() throws TimelessTrapException {
        while (!explorationQueue.isEmpty()) {
            State state = explorationQueue.poll();
            successorRates.clear();
            for (State successor : explorerUtilities.getSuccessors(state).keySet()) {
                double rate = rate(state, successor);
                if (successor.isTangible()) {
                    registerStateTransition(state, successor, rate);
                } else {
                    Collection<StateRateRecord> explorableStates = vanishingExplorer.explore(successor, rate);
                    for (StateRateRecord record : explorableStates) {
                        registerStateTransition(state, record.getState(), record.getRate());
                    }
                }
            }
            writeStateTransitions(state);
        }
    }

    /**
     * This method writes all state transitions in the map stateTransitions out to the
     * state writer.
     *
     * It is assumed that no duplicate transitions exist by this point and that their rates
     * have been summed up. If this is not the case then multiple transitions will be written to
     * disk and must be dealt with accordingly.
     *
     * @param state the current state that successors belong to
     */
    private void writeStateTransitions(State state) {
        for (Map.Entry<State, Double> entry : successorRates.entrySet()) {
            State successor = entry.getKey();
            double rate = entry.getValue();
            stateWriter.transition(state, successor, rate);
        }
    }

    /**
     * registers a transition to the successor in stateRecords and
     * adds the successor to the exploredQueue if it is not already contained in it.
     *
     *
     * @param state current state
     * @param successor state that is possible via an enabled transition from state
     * @param rate rate at which state transitions to successor
     */
    private void registerStateTransition(State state, State successor, double rate) {
        registerStateRate(successor, rate);
        if (!explored.contains(successor)) {
            explorationQueue.add(successor);
            markAsExplored(successor);
        }
    }

    /**
     * Register the successor into successorRates map
     *
     * If successor already exists then the rate is summed, if not it
     * is added as a new entry
     * @param successor key to successor rates
     * @param rate rate at which successor is entered via some transition
     */
    private void registerStateRate(State successor, double rate) {
        if (successorRates.containsKey(successor)) {
            double previousRate = successorRates.get(successor);
            successorRates.put(successor, previousRate + rate);
        } else {
            successorRates.put(successor, rate);
        }
    }


    /**
     * Calculates the rate of a  transition from a tangible state to the successor state.
     * It does this by calculating the transitions that are enabled at the given state,
     * the transitions that can be reached from that state and performs the intersection of the two.
     * <p/>
     * It then sums the firing rates of this intersection and divides by the sum of the firing rates
     * of the enabled transition
     */
    private double rate(State state, State successor) {
        Collection<Transition> transitionsToSuccessor = explorerUtilities.getTransitions(state, successor);
        return explorerUtilities.getWeightOfTransitions(transitionsToSuccessor);
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
    private void exploreInitialState(State initialState) throws TimelessTrapException {
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

    /**
     * Adds a compressed version of a tangible state to exploredStates
     *
     * @param state
     */
    //TODO: IMPLEMENT COMPRESSED VERSION
    private void markAsExplored(State state) {
        explored.add(state);
    }

}
