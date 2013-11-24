/*
 * transitionPriorityEdit.java
 */
package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.component.Transition;

/**
 * In charge of setting {@link Transition} angle
 */
public class TransitionRotation
        extends HistoryItem
{

    private final int newAngle;
    private final Transition transition;
    private final int oldAngle;

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
        if (transition != null ? !transition.equals(that.transition) :
                that.transition != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = newAngle;
        result = 31 * result + (transition != null ? transition.hashCode() : 0);
        result = 31 * result + oldAngle;
        return result;
    }

    public TransitionRotation(final Transition transition, final int oldAngle,
                              final int newAngle) {

        this.transition = transition;
        this.oldAngle = oldAngle;
        this.newAngle = newAngle;
    }

    /** */
   public void undo() {
       transition.setAngle(oldAngle);
   }

   
   /** */
   public void redo() {
       transition.setAngle(newAngle);
   }
   
}
