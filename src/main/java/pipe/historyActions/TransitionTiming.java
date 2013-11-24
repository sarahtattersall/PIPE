/*
 * TransitionTimingEdit.java
 */
package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.Transition;
import pipe.views.TransitionView;

/**
 * HistoryItem in charge of whether a {@link Transition} is timed or not
 */
public class TransitionTiming extends HistoryItem {

    private final Transition transition;
    private final PetriNet petriNet;
    private final boolean timedValue;

    public TransitionTiming(final Transition transition,
                            final PetriNet petriNet, final boolean timedValue) {

        this.transition = transition;
        this.petriNet = petriNet;
        this.timedValue = timedValue;
    }

    /** */
    public void undo() {
        transition.setTimedTransition(!timedValue);
        petriNet.notifyObservers();
    }

    /** */
    public void redo() {
        transition.setTimedTransition(timedValue);
        petriNet.notifyObservers();
    }

}
