/*
 * TransitionTimingEdit.java
 */
package pipe.historyActions;

import pipe.models.component.Transition;

/**
 * HistoryItem in charge of whether a {@link Transition} is timed or not
 */
public class TransitionTiming extends HistoryItem {

    private final Transition transition;
    private final boolean timedValue;

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
        if (transition != null ? !transition.equals(that.transition) :
                that.transition != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = transition != null ? transition.hashCode() : 0;
        result = 31 * result + (timedValue ? 1 : 0);
        return result;
    }

    public TransitionTiming(final Transition transition, final boolean timedValue) {

        this.transition = transition;
        this.timedValue = timedValue;
    }

    /** */
    public void undo() {
        transition.setTimed(!timedValue);
    }

    /** */
    public void redo() {
        transition.setTimed(timedValue);
    }

}
