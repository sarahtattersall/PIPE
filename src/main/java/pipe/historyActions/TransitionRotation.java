/*
 * transitionPriorityEdit.java
 */
package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.Transition;
import pipe.views.TransitionView;

/**
 * In charge of setting {@link Transition} angle
 */
public class TransitionRotation
        extends HistoryItem
{

    private final int newAngle;
    private final Transition transition;
    private final PetriNet petriNet;
    private final int oldAngle;

    public TransitionRotation(final Transition transition,
                              final PetriNet petriNet, final int oldAngle,
                              final int newAngle) {

        this.transition = transition;
        this.petriNet = petriNet;
        this.oldAngle = oldAngle;
        this.newAngle = newAngle;
    }

    /** */
   public void undo() {
       transition.setAngle(oldAngle);
       petriNet.notifyObservers();
   }

   
   /** */
   public void redo() {
       transition.setAngle(newAngle);
       petriNet.notifyObservers();
   }
   
}
