package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.Transition;
import pipe.views.TransitionView;

public class TransitionPriority extends HistoryItem
{
    private final Transition transition;
    private final PetriNet petriNet;
    private final int oldPriority;
    private final int newPriority;

    public TransitionPriority(final Transition transition,
                              final PetriNet petriNet, final int oldPriority,
                              final int newPriority) {

        this.transition = transition;
        this.petriNet = petriNet;
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
    }

    public void undo()
    {
        transition.setPriority(oldPriority);
        petriNet.notifyObservers();
    }

    public void redo()
    {
        transition.setPriority(newPriority);
        petriNet.notifyObservers();
    }

}
