/*
 * transitionPriorityEdit.java
 */
package pipe.historyActions;

import pipe.models.component.transition.Transition;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * In charge of setting {@link Transition} angle
 */
public class TransitionRotation extends AbstractUndoableEdit {

    private final int newAngle;

    private final Transition transition;

    private final int oldAngle;

    public TransitionRotation(final Transition transition, final int oldAngle, final int newAngle) {

        this.transition = transition;
        this.oldAngle = oldAngle;
        this.newAngle = newAngle;
    }

    @Override
    public int hashCode() {
        int result = newAngle;
        result = 31 * result + (transition != null ? transition.hashCode() : 0);
        result = 31 * result + oldAngle;
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

        final TransitionRotation that = (TransitionRotation) o;

        if (newAngle != that.newAngle) {
            return false;
        }
        if (oldAngle != that.oldAngle) {
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
        transition.setAngle(oldAngle);
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        transition.setAngle(newAngle);
    }

}
