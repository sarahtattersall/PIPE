/*
 * TransitionTimingEdit.java
 */
package pipe.historyActions.transition;

import pipe.models.component.transition.Transition;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * HistoryItem in charge of whether a {@link Transition} is timed or not
 */
public class TransitionTiming extends AbstractUndoableEdit {

    private final Transition transition;

    private final boolean timedValue;

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

    /** */
    @Override
    public void undo() {
        super.undo();
        transition.setTimed(!timedValue);
    }

    /** */
    @Override
    public void redo() {
        super.redo();
        transition.setTimed(timedValue);
    }

}
