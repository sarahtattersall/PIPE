package pipe.reachability.algorithm.sequential;

import pipe.models.component.transition.Transition;
import pipe.reachability.algorithm.*;
import pipe.reachability.algorithm.state.StateExplorer;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import pipe.reachability.state.State;

import java.io.ObjectOutputStream;
import java.util.*;

/**
 * This class performs state space exploration sequentially to determine the reachability of each state
 * <p/>
 * It performs on the fly vanishing state elimination, producing the reachability graph for tangible states.
 * A tangible state is one in which:
 * a) Has no enabled transitions
 * b) Has entirely timed transitions leaving it
 * <p/>
 * A vanishing state is therefore one where there are immediate enabled transitions out of it. It can be eliminated
 * because no amount time is spent in this state (since there is an immediate transition out of it). This optimisation
 * reduces the memory needed to store the state space.
 */
public class SequentialStateSpaceExplorer implements StateSpaceExplorer {

    /**
     * Queue for tangible states yet to be explored
     */
    Queue<State> tangibleQueue = new ArrayDeque<>();

    /**
     * Contains tangible states that have already been explored.
     */
    private Set<State> explored = new HashSet<>();

    private final StateExplorer tangibleExplorer;
    private final VanishingExplorer vanishingExplorer;
    private final ExplorerUtilities explorerUtilities;


    public SequentialStateSpaceExplorer(StateExplorer tangibleExplorer, VanishingExplorer vanishingExplorer,
                                        ExplorerUtilities explorerUtilities) {
        this.tangibleExplorer = tangibleExplorer;
        this.vanishingExplorer = vanishingExplorer;
        this.explorerUtilities = explorerUtilities;
    }


    /**
     * Performs state space exploration writing the results to the Writer stream.
     * That is it writes the transitions from each state to the writer.
     *
     * @param writer writer in which to write the output to
     */
    @Override
    public void generate(ObjectOutputStream writer) throws TimelessTrapException {
        clearDataStructures();
        State initialState = explorerUtilities.createState();
        exploreInitialState(initialState);
        stateSpaceExploration();
    }

    /**
     * Clears any persistent data structures
     */
    private void clearDataStructures() {
        tangibleQueue.clear();
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
        while (!tangibleQueue.isEmpty()) {
            State state = tangibleQueue.poll();
            for (State successor : explorerUtilities.getSuccessors(state).keySet()) {
                double rate = rate(state, successor);
                if (successor.isTangible()) {
                    tangibleExplorer.explore(state, successor, rate);
                    if (!explored.contains(successor)) {
                        tangibleQueue.add(successor);
                        markAsExplored(successor);
                    }
                } else {
                    Collection<State> tangibleStates = vanishingExplorer.explore(state, successor, rate);
                    for (State tangible : tangibleStates) {
                        if (!explored.contains(tangible)) {
                            tangibleQueue.add(tangible);
                            markAsExplored(tangible);
                        }
                    }
                }
            }
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
            tangibleQueue.add(initialState);
            markAsExplored(initialState);
        } else {
            vanishingExplorer.explore(null, initialState, 1.0);
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
