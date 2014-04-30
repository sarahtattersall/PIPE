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
        }
    }

    /**
     * Registers a transition from state to successor with rate
     *
     * Writes out the result.
     *
     * @param state this field can be null to represent that successor is the root of the tree
     * @param successor
     * @param rate rate at which state transitions to successor
     */
    private void registerStateTransition(State state, State successor, double rate) {
        stateWriter.transition(state, successor, rate);
        if (!explored.contains(successor)) {
            explorationQueue.add(successor);
            markAsExplored(successor);
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
