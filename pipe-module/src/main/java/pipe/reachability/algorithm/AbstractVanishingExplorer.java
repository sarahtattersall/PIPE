package pipe.reachability.algorithm;

import pipe.models.component.transition.Transition;
import pipe.reachability.state.State;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Abstract class that contains the shared code for vanishing explorers
 *
 * The difference between the explorers is what happens when a tangible state is found
 * The recorded state is either the last tanigble or the parent vanishin state.
 *
 * Differing implementations can choose which state to explore
 */
public abstract class AbstractVanishingExplorer implements VanishingExplorer {
    /**
     * The number of times that cyclic vanishing states are allowed to be explored before a
     * {@link pipe.reachability.algorithm.TimelessTrapException} is thrown
     */
    //TODO ASK WILL FOR THE SIZE?
    private static final int ALLOWED_ITERATIONS = 10000;

    /**
     * Value used to eliminate a vanishing state. We do not explore a state if the rate into it is
     * less than this value
     */
    private static final double EPSILON = 0.0000001;

    /**
     * Explores a vanishing state
     */
    private final StateExplorer vanishingExplorer;

    /**
     * Explorer utilities useful for state manipulations
     */
    private final ExplorerUtilites explorerUtilities;


    protected AbstractVanishingExplorer(StateExplorer vanishingExplorer, ExplorerUtilites explorerUtilities) {
        this.vanishingExplorer = vanishingExplorer;
        this.explorerUtilities = explorerUtilities;
    }

    @Override
    public Collection<State> explore(State lastTangible, State vanishingState, double rate) throws TimelessTrapException {
        Deque<VanishingRecord> vanishingStack = new ArrayDeque<>();
        vanishingStack.push(new VanishingRecord(vanishingState, rate));
        int iterations = 0;
        Collection<State> tangibleStatesFound = new LinkedList<>();
        while (!vanishingStack.isEmpty() && iterations < ALLOWED_ITERATIONS) {
            VanishingRecord record = vanishingStack.pop();
            State previous = record.getState();
            for (State successor : explorerUtilities.getSuccessors(previous).keySet()) {
                double successorRate = record.getRate() * probability(previous, successor);
                if (successor.isTangible()) {
                    tangibleStatesFound.add(successor);
                    registerTangible(lastTangible, previous, successor, successorRate);
                } else {
                    vanishingExplorer.explore(previous, successor, rate);
                    if (successorRate > EPSILON) {
                        vanishingStack.push(new VanishingRecord(successor, successorRate));
                    }

                }
            }
            iterations++;
        }
        if (iterations == ALLOWED_ITERATIONS) {
            throw new TimelessTrapException();
        }
        return tangibleStatesFound;
    }


    /**
     * Works out what transitions would lead you to the successor state then divides the sum
     * of their rates by the total rates of all enabled transitions
     *
     * @param state     initial state
     * @param successor next state
     * @return the probability of transitioning to the successor state from state
     */
    private double probability(State state, State successor) {
        Collection<Transition> marked = explorerUtilities.getTransitions(state, successor);
        if (marked.isEmpty()) {
            return 0;
        }
        double toSuccessorWeight = explorerUtilities.getWeightOfTransitions(marked);
        double totalWeight =
                explorerUtilities.getWeightOfTransitions(explorerUtilities.getAllEnabledTransitions(state));
        return toSuccessorWeight / totalWeight;
    }


    /**
     *
     * Registers the tangible state
     *
     * @param lastTangible
     * @param previous
     * @param successor
     * @param rate
     */
    protected abstract void registerTangible(State lastTangible, State previous, State successor, double rate);
}
