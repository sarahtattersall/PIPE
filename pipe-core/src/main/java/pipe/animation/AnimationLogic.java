package pipe.animation;

import pipe.models.component.transition.Transition;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Contains methods which deal with the logic of animation, for instance
 * which transitions are enabled for a given state.
 */
public interface AnimationLogic {
    /**
     * @param state Must be a valid state for the Petri net this class represents
     * @return all enabled transitions
     */
    Set<Transition> getEnabledTransitions(State state);

    /**
     * Calculates successor states of a given state
     *
     * @param state
     * @return successors of the given state
     */
    Map<State, Collection<Transition>> getSuccessors(State state);

    /**
     *
     * @param state
     * @param transition
     * @return the successor state after firing the transition
     */
    State getFiredState(State state, Transition transition);
}
