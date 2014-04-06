package pipe.historyActions;

import pipe.models.component.transition.Transition;

import java.util.*;

/**
 * AnimationHistory for an individual PetriNet
 */
public class AnimationHistory extends Observable {
    /**
     * List to hold transitions fired in their order
     * Used for going back/forward in time
     */
    private List<Transition> firingSequence = new ArrayList<>();

    /**
     * Current index of the firingSequence;
     * Initialised to -1 so when the first item is added it points to zero
     */
    private int currentPosition = -1;


    /**
     * Cannot step forward if head of the list
     * @return
     */
    public boolean isStepForwardAllowed() {
        return currentPosition < firingSequence.size() - 1;
    }


    /**
     * Can step back if currentPosition points to any transitions
     * @return
     */
    public boolean isStepBackAllowed() {
        return currentPosition >= 0;
    }

    public void stepForward() {
        if (isStepForwardAllowed()) {
            currentPosition++;
            flagChanged();
        }
    }


    public void stepBackwards() {
        if (isStepBackAllowed()) {
            currentPosition--;
            flagChanged();
        }
    }

    /**
     * Remove all steps past the current step
     */
    public void clearStepsForward() {
        if (currentPosition >= -1 && currentPosition + 1 < firingSequence.size()) {
            while (firingSequence.size() > currentPosition + 1) {
                firingSequence.remove(firingSequence.size() - 1);
            }
        }
    }

    public List<Transition> getFiringSequence() {
        return firingSequence;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void addHistoryItem(Transition transition) {
        firingSequence.add(transition);
        currentPosition++;
        flagChanged();
    }

    public Transition getCurrentTransition() {
        if (currentPosition >= 0) {
            return firingSequence.get(currentPosition);
        }
        throw new RuntimeException("No transitions in history");
    }

    public Transition getTransition(int index) {
        if (index <= firingSequence.size()) {
            return firingSequence.get(index);
        }
        throw new RuntimeException("Index is greater than number of transitions stored");
    }

    public void clear() {
        currentPosition = -1;
        firingSequence.clear();
        flagChanged();
    }

    /**
     * Rolls the setting changed and notifying observers into one method call.
     * It tells Observers that it has changed
     */
    private void flagChanged() {
        setChanged();
        notifyObservers();
    }
}
