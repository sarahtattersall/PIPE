package pipe.reachability.algorithm;

import pipe.animation.Animator;
import pipe.animation.PetriNetAnimator;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.FunctionalResults;
import pipe.parsers.PetriNetWeightParser;
import pipe.reachability.io.WriterFormatter;
import pipe.reachability.state.HashedState;
import pipe.reachability.state.State;
import pipe.visitor.ClonePetriNet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * This class performs state space exploration to determine the reachability of each state
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
public class Reachability {

    /**
     * Value used to eliminate a vanishing state. We do not explore a state if the rate into it is
     * less than this value
     */
    private static final double EPSILON = 0.0000001;

    /**
     * The number of times that cyclic vanishing states are allowed to be explored before a
     * {@link pipe.reachability.algorithm.TimelessTrapException} is thrown
     */
    //TODO AS WILL FOR THE SIZE?
    private static final int ALLOWED_ITERATIONS = 10000;

    /**
     * PetriNet to generate reachability graph for
     */
    private final PetriNet petriNet;

    /**
     * Token to perform reachability graph on
     */
    private final Token token;

    /**
     * Form in which to write transitions out to a Writer
     */
    private final WriterFormatter formatter;

    /**
     * Responsible for firing transitions in the Petri net. It is used to determine what transitions
     * are enabled for a given state
     */
    private final Animator animator;

    Queue<State> tangibleQueue = new ArrayDeque<>();

    /**
     * Contains tangilble states that have already been explored.
     */
    private Set<State> explored = new HashSet<>();

    /**
     * Cached successors is used when exploring states to quickly determine
     * a states successors it has already seen before.
     * <p/>
     * It will be most useful when exploring cyclic transitions
     */
    private Map<State, Map<State, Collection<Transition>>> cachedSuccessors = new HashMap<>();


    public Reachability(PetriNet petriNet, Token token, WriterFormatter formatter) {
        this.token = token;
        this.formatter = formatter;
        this.petriNet = ClonePetriNet.clone(petriNet);
        animator = new PetriNetAnimator(this.petriNet);
    }


    /**
     * Performs state space exploration writing the results to the Writer stream.
     * That is it writes the transitions from each state to the writer.
     *
     * @param writer writer in which to write the output to
     */
    public void generate(OutputStream writer) throws TimelessTrapException {
        clearDataStructures();
        State initialState = createState();
        exploreInitialState(initialState);
        stateSpaceExploration(writer);
    }

    /**
     * Clears any persistent data structures
     */
    private void clearDataStructures() {
        tangibleQueue.clear();
        explored.clear();
        cachedSuccessors.clear();
    }

    /**
     * Performs state space exploration of the tangibleQueue
     * popping a state off the stack and exploring all its successors.
     * <p/>
     * It records the reachability graph into the writer
     *
     * @param writer in which to record the reachability graph
     */
    private void stateSpaceExploration(OutputStream writer) throws TimelessTrapException {
        while (!tangibleQueue.isEmpty()) {
            State state = tangibleQueue.poll();
            for (State successor : getSuccessors(state).keySet()) {
                double rate = rate(state, successor);
                if (isTangible(successor)) {
                    transition(state, successor, rate, writer);
                    if (!explored.contains(successor)) {
                        tangibleQueue.add(successor);
                        markAsExplored(successor);
                    }
                } else {
                    exploreVanishingStates(successor, rate, new SaveStateTangibleAction(writer, state));
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
        Collection<Transition> transitionsToSuccessor = getTransitions(state, successor);
        return getWeightOfTransitions(transitionsToSuccessor);
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
        if (isTangible(initialState)) {
            tangibleQueue.add(initialState);
            markAsExplored(initialState);
        } else {
            exploreVanishingStates(initialState, 1.0, new PerformInitialTangibleAction());
        }

    }

    /**
     * Explores vanishing states in the tree. Adds any tangible states it finds
     * to the tangible exploration queue
     * <p/>
     * Cycles of vanishing states can be a problem since there is no explored table
     * for vanishing states. In order to ensure there is no infinite loop of exploration
     * states whose propagated effective entry rate falls below a certain threshold (EPSILON)
     * is dropped.
     * <p/>
     * Finally strongly connected components of vanishing states are known as timeless traps
     * They correspond to functional errors in the Markov Chain. In order to avoid this
     * an elimination timeout is applied which will expire after a number of attempts have
     * been made to eliminate a cluster of states/
     *
     * @param state                 state to explore from
     * @param rate
     * @param performTangibleAction action performed when finding a tangible state
     */
    private void exploreVanishingStates(State state, double rate, PerformTangibleAction performTangibleAction)
            throws TimelessTrapException {
        Deque<VanishingRecord> vanishingStack = new ArrayDeque<>();
        vanishingStack.push(new VanishingRecord(state, rate));
        int iterations = 0;
        while (!vanishingStack.isEmpty() && iterations < ALLOWED_ITERATIONS) {
            VanishingRecord record = vanishingStack.pop();
            for (State successor : getSuccessors(record.getState()).keySet()) {
                double successorRate = record.getRate() * probability(record.getState(), successor);
                if (isTangible(successor)) {
                    performTangibleAction.performAction(successor, successorRate);
                } else {
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
    }

    /**
     * Saves out a transition from state to successor to the writer
     *
     * @param state
     * @param successor
     * @param successorRate
     * @param writer
     */
    private void transition(State state, State successor, double successorRate, OutputStream writer) {
        try {
            formatter.write(state, successor, successorRate, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Collection<Transition> marked = getTransitions(state, successor);
        if (marked.isEmpty()) {
            return 0;
        }
        double toSuccessorWeight = getWeightOfTransitions(marked);
        setState(state);
        double totalWeight = getWeightOfTransitions(animator.getEnabledTransitions());
        return toSuccessorWeight / totalWeight;
    }

    private double getWeightOfTransitions(Iterable<Transition> transitions) {
        double weight = 0;
        PetriNetWeightParser parser = new PetriNetWeightParser(petriNet);
        for (Transition transition : transitions) {
            FunctionalResults<Double> results = parser.evaluateExpression(transition.getRateExpr());
            if (!results.hasErrors()) {
                weight += results.getResult();
            } else {
                //TODO:
            }
        }
        return weight;
    }

    /**
     * Calculates the set of transitions that will take you from one state to the successor.
     *
     * @param state     initial state
     * @param successor successor state, must be directly reachable from the state
     * @return enabled transitions that take you from state to successor, if it is not directly reachable then
     * an empty Collection will be returned
     */
    private Collection<Transition> getTransitions(State state, State successor) {
        Map<State, Collection<Transition>> stateTransitions = getSuccessors(state);
        if (stateTransitions.containsKey(successor)) {
            return stateTransitions.get(successor);
        }
        return new LinkedList<>();
    }


    /**
     * Calculates successor states of the given state. Uses memoization to avoid calculating successors twice
     * however this is at the expense of extra memory
     *
     * @param state current state
     * @return Map of successor states to the transition that caused them
     */
    private Map<State, Collection<Transition>> getSuccessors(State state) {

        if (cachedSuccessors.containsKey(state)) {
            return cachedSuccessors.get(state);
        }

        setState(state);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        Map<State, Collection<Transition>> successors = new HashMap<>();
        for (Transition transition : enabled) {
            setState(state);
            animator.fireTransition(transition);
            State successor = createState();


            if (!successors.containsKey(successor)) {
                successors.put(successor, new LinkedList<Transition>());
            }
            successors.get(successor).add(transition);
        }
        cachedSuccessors.put(state, successors);
        return successors;
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

    /**
     * Creates a new state containing the token counts for the
     * current Petri net.
     *
     * @return current state of the Petri net
     */
    private State createState() {
        Map<String, Integer> tokenCounts = new HashMap<>();
        for (Place place : petriNet.getPlaces()) {
            tokenCounts.put(place.getId(), place.getTokenCount(token));
        }
        return new HashedState(tokenCounts);
    }

    /**
     * Sets the Petri net to this state
     *
     * @param state contains the token counts to set the places to
     */
    private void setState(State state) {
        for (Place place : petriNet.getPlaces()) {
            place.setTokenCount(token, state.getTokens(place.getId()));
        }
    }

    /**
     * A tangible state is one in which:
     * a) Has no enabled transitions
     * b) Has entirely timed transitions leaving it
     *
     * @param state to test for tangibility
     * @return true if the current state is tangible
     */
    private boolean isTangible(State state) {
        setState(state);
        Set<Transition> enabledTransitions = animator.getEnabledTransitions();
        boolean anyTimed = false;
        boolean anyImmediate = false;
        for (Transition transition : enabledTransitions) {
            if (transition.isTimed()) {
                anyTimed = true;
            } else {
                anyImmediate = true;
            }
        }
        return enabledTransitions.isEmpty() || (anyTimed && !anyImmediate);
    }

    /**
     * Performs action on finding that a successor state is tangible
     */
    private static interface PerformTangibleAction {
        void performAction(State successor, double successorRate);
    }

    /**
     * Adds the successor to the tangible queue for exploration
     */
    private class PerformInitialTangibleAction implements PerformTangibleAction {

        @Override
        public void performAction(State successor, double successorRate) {
            if (!explored.contains(successor)) {
                tangibleQueue.add(successor);
                markAsExplored(successor);
            }
        }
    }

    /**
     * Wraps {@link Reachability.PerformInitialTangibleAction} saving the state out
     * to a writer
     */
    private class SaveStateTangibleAction implements PerformTangibleAction {
        /**
         * Writer to write results to
         */
        private final OutputStream writer;

        /**
         * Tangible state that a transition occurs from
         */
        private final State state;

        /**
         * Wraps the initial action
         */
        PerformInitialTangibleAction action = new PerformInitialTangibleAction();

        private SaveStateTangibleAction(OutputStream writer, State state) {
            this.writer = writer;
            this.state = state;
        }

        @Override
        public void performAction(State successor, double successorRate) {
            action.performAction(successor, successorRate);
            transition(state, successor, successorRate, writer);
        }
    }


}
