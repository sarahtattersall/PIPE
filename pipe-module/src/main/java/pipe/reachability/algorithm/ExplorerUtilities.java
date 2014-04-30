package pipe.reachability.algorithm;

import pipe.animation.Animator;
import pipe.models.component.transition.Transition;
import pipe.reachability.state.State;

import java.util.Collection;
import java.util.Map;

public interface ExplorerUtilities {
    Map<State, Collection<Transition>> getSuccessors(State state);

    State createState();

    Collection<Transition> getTransitions(State state, State successor);

    double getWeightOfTransitions(Iterable<Transition> transitions);

    Collection<Transition> getAllEnabledTransitions(State state);
}
