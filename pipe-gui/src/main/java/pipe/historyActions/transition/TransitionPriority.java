package pipe.historyActions.transition;

import pipe.models.component.transition.Transition;

import javax.swing.undo.AbstractUndoableEdit;

public class TransitionPriority extends AbstractUndoableEdit {
    private final Transition transition;

    private final int oldPriority;

    private final int newPriority;

    public TransitionPriority(final Transition transition, final int oldPriority, final int newPriority) {

        this.transition = transition;
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
    }

    @Override
    public int hashCode() {
        int result = transition.hashCode();
        result = 31 * result + oldPriority;
        result = 31 * result + newPriority;
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

        final TransitionPriority priority = (TransitionPriority) o;

        if (newPriority != priority.newPriority) {
            return false;
        }
        if (oldPriority != priority.oldPriority) {
            return false;
        }
        if (!transition.equals(priority.transition)) {
            return false;
        }

        return true;
    }

    @Override
    public void undo() {
        super.undo();
        transition.setPriority(oldPriority);
    }

    @Override
    public void redo() {
        super.redo();
        transition.setPriority(newPriority);
    }

}
