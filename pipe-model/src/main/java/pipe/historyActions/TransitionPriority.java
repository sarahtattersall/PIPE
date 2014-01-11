package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.component.Transition;

public class TransitionPriority extends HistoryItem
{
    private final Transition transition;
    private final int oldPriority;
    private final int newPriority;

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
    public int hashCode() {
        int result = transition.hashCode();
        result = 31 * result + oldPriority;
        result = 31 * result + newPriority;
        return result;
    }

    public TransitionPriority(final Transition transition, final int oldPriority,
                              final int newPriority) {

        this.transition = transition;
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
    }

    public void undo()
    {
        transition.setPriority(oldPriority);
    }

    public void redo()
    {
        transition.setPriority(newPriority);
    }

}
