/*
 * TransitionServerSemanticEdit.java
 */
package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.Transition;
import pipe.views.TransitionView;

/**
 * Controls if a transition is an infinite server
 */
public class TransitionInfiniteServer
        extends HistoryItem
{

    private final Transition transition;
    private final PetriNet petriNet;
    private final boolean value;

    public TransitionInfiniteServer(final Transition transition,
                                    final PetriNet petriNet,
                                    final boolean value) {

        this.transition = transition;
        this.petriNet = petriNet;
        this.value = value;
    }

    /** */
   public void undo() {
      transition.setInfiniteServer(!value);
       petriNet.notifyObservers();
   }

   
   /** */
   public void redo() {
       transition.setInfiniteServer(value);
       petriNet.notifyObservers();
   }
   
}
