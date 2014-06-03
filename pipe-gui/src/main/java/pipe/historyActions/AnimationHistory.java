package pipe.historyActions;

import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.List;

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
     * Steps forward
     */
    void stepForward();

    void stepBackwards();

    /**
     * Remove all steps past the current step
     */
    void clearStepsForward();

    List<Transition> getFiringSequence();

    int getCurrentPosition();

    void addHistoryItem(Transition transition);

    Transition getCurrentTransition();

    Transition getTransition(int index);

    void clear();
}
