package pipe.historyActions;

import pipe.models.component.Transition;
import pipe.models.interfaces.IObservable;
import pipe.models.interfaces.IObserver;

import java.util.*;

/**
 * AnimationHistory for an individual PetriNet
 */
public class AnimationHistory implements IObservable {
    /**
     * List to hold transitions fired in their order
     * Used for going back/forward in time
     */
    private List<Transition> firingSequence = new ArrayList<Transition>();

    /**
     * Current index of the firingSequence;
     * Initialised to -1 so when the first item is added it points to zero
     */
    private int currentPosition = -1;


    private Set<IObserver> observers = new HashSet<IObserver>();

    @Override
    public void registerObserver(final IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(final IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

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
        }
        notifyObservers();
    }


    public void stepBackwards() {
        if (isStepBackAllowed()) {
            currentPosition--;
        }
        notifyObservers();
    }

    public void clearStepsForward() {
        if (currentPosition > 0 && currentPosition + 1 < firingSequence.size()) {
            Collection<Transition> remove = new LinkedList<Transition>();
            for (int i = currentPosition + 1; i < firingSequence.size(); i++) {
                remove.add(firingSequence.get(i));
            }
            firingSequence.removeAll(remove);
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
        notifyObservers();
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
        notifyObservers();
    }
}
