package pipe.reachability.algorithm;

import pipe.models.component.transition.Transition;
import pipe.reachability.state.ExplorerState;

import java.util.Collection;
import java.util.Map;

/**
 * Useful class for performing state calculations on a Petri net
 */
public interface ExplorerUtilities {
    /**
     *
     * Finds successors of the given state. A successor is a state that occurs
     * when one of the enabled transitions in the current state is fired.
     *
     * @param state state in the Petri net to find successors of
     * @return map of successor states to the transitions that caused them
     */
    Map<ExplorerState, Collection<Transition>> getSuccessorsWithTransitions(ExplorerState state);


    /**
     *
     * Finds successors of the given state. A successor is a state that occurs
     * when one of the enabled transitions in the current state is fired.
     *
     * @param state state in the Petri net to find successors of
     * @return map of successor states to the transitions that caused them
     */
    Collection<ExplorerState> getSuccessors(ExplorerState state);



    /**
     * Calculates the rate of a  transition from a tangible state to the successor state.
     * It does this by calculating the transitions that are enabled at the given state,
     * the transitions that can be reached from that state and performs the intersection of the two.
     * <p/>
     * It then sums the firing rates of this intersection and divides by the sum of the firing rates
     * of the enabled transition
     */
    double rate(ExplorerState state, ExplorerState successor);

    /**
     *
     * Calculates the current underling state of the Petri net
     * and creates a new state.
     *
     * It determines if it is a vanishing or tangible transition and returns
     * the correct implementation accordingly.
     *
     * @return underlying state of the Petri net
     */
    ExplorerState getCurrentState();

    /**
     * Calculates the set of transitions that will take you from one state to the successor.
     *
     * @param state     initial state
     * @param successor successor state, must be directly reachable from the state
     * @return enabled transitions that take you from state to successor, if it is not directly reachable then
     * an empty Collection will be returned
     */
    Collection<Transition> getTransitions(ExplorerState state, ExplorerState successor);

    /**
     *
     * Sums up the weights of the transitions. Transitions may have functional rates
     *
     * @param transitions
     * @return summed up the weight of the transitions specified
     */
    double getWeightOfTransitions(Iterable<Transition> transitions);



    /**
     *
     * @param state state in the Petri net to determine enabled transitions of
     * @return all enabled transitions for the specified state
     */
    Collection<Transition> getAllEnabledTransitions(ExplorerState state);

    void clear();
}
