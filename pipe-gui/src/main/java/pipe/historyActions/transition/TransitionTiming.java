/*
 * TransitionTimingEdit.java
 */
package pipe.historyActions.transition;


import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undo item for changing whether a transition is timed or immediate
 */
public class TransitionTiming extends AbstractUndoableEdit {

    /**
     * Underlying transition model
     */
    private final Transition transition;

    /**
     * True if timed
     */
    private final boolean timedValue;

    /**
     * Constructor
     *
     * @param transition underlying transition model
     * @param timedValue true if timed
     */
    public TransitionTiming(final Transition transition, final boolean timedValue) {

        this.transition = transition;
        this.timedValue = timedValue;
    }

    @Override
    public int hashCode() {
        int result = transition != null ? transition.hashCode() : 0;
        result = 31 * result + (timedValue ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TransitionTiming that = (TransitionTiming) o;

        if (timedValue != that.timedValue) {
            return false;
        }
        if (transition != null ? !transition.equals(that.transition) : that.transition != null) {
            return false;
        }

        return true;
    }

    /**
     * Toggles the transition to its previous timed value
     */
    @Override
    public void undo() {
        super.undo();
        transition.setTimed(!timedValue);
    }

    /**
     * Toggles the transition to its new timed value
     */
    @Override
    public void redo() {
        super.redo();
        transition.setTimed(timedValue);
    }

}
