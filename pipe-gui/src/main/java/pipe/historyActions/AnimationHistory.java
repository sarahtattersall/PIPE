package pipe.historyActions;

import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.List;

/**
 * History manager for stepping through animation actions when the Petri net is in animation mode
 */
public interface AnimationHistory {
    /**
     * Cannot step forward if head of the list
     * @return true if stepping forward within the animation is allowed, that is if there are transition firings to redo
     */
    boolean isStepForwardAllowed();

    /**
     * Can step back if currentPosition points to any transitions
     * @return  true if stepping backward within the animation is allowed, that is if there are transition firings to undo
     */
    boolean isStepBackAllowed();

    /**
     * Steps forward, updating the current item
     */
    void stepForward();

    /**
     * Steps backward, updating the current item
     */
    void stepBackwards();

    /**
     * Remove all steps past the current step
     */
    void clearStepsForward();

    /**
     *
     * @return a list of transitions fired
     */
    List<Transition> getFiringSequence();

    /**
     *
     * @return current position in the firing sequence
     */
    int getCurrentPosition();

    /**
     *
     * Register that this transition has been fired and create a history item for it
     * @param transition
     */
    void addHistoryItem(Transition transition);

    /**
     *
     * @return transition at current position in the firing sequence
     */
    Transition getCurrentTransition();

    /**
     *
     * @param index
     * @return transition at this index in the firing sequence
     */
    Transition getTransition(int index);

    void clear();
}
