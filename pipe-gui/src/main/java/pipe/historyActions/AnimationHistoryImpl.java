package pipe.historyActions;


import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * AnimationHistory for an individual PetriNet
 */
public final class AnimationHistoryImpl extends Observable implements AnimationHistory {
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
     * @return true if stepping forward within the animation is allowed, that is if there are transition firings to redo
     */
    @Override
    public boolean isStepForwardAllowed() {
        return currentPosition < firingSequence.size() - 1;
    }


    /**
     * Can step back if currentPosition points to any transitions
     * @return  true if stepping backward within the animation is allowed, that is if there are transition firings to undo
     */
    @Override
    public boolean isStepBackAllowed() {
        return currentPosition >= 0;
    }

    /**
     * Steps forward
     */
    @Override
    public void stepForward() {
        if (isStepForwardAllowed()) {
            currentPosition++;
            flagChanged();
        }
    }


    @Override
    public void stepBackwards() {
        if (isStepBackAllowed()) {
            currentPosition--;
            flagChanged();
        }
    }

    /**
     * Remove all steps past the current step
     */
    @Override
    public void clearStepsForward() {
        if (currentPosition >= -1 && currentPosition + 1 < firingSequence.size()) {
            while (firingSequence.size() > currentPosition + 1) {
                firingSequence.remove(firingSequence.size() - 1);
            }
        }
    }

    @Override
    public List<Transition> getFiringSequence() {
        return firingSequence;
    }

    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void addHistoryItem(Transition transition) {
        firingSequence.add(transition);
        currentPosition++;
        flagChanged();
    }

    @Override
    public Transition getCurrentTransition() {
        if (currentPosition >= 0) {
            return firingSequence.get(currentPosition);
        }
        throw new RuntimeException("No transitions in history");
    }

    @Override
    public Transition getTransition(int index) {
        if (index <= firingSequence.size()) {
            return firingSequence.get(index);
        }
        throw new RuntimeException("Index is greater than number of transitions stored");
    }

    @Override
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
