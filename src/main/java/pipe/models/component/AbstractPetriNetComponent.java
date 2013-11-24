package pipe.models.component;

import pipe.models.Observable;
import pipe.models.interfaces.IObserver;

/**
 * This class implements the IObserverbale pattern and acts as a wrapper
 * for {@link pipe.models.Observable}
 */
public abstract class AbstractPetriNetComponent implements PetriNetComponent {

    private final Observable observable = new Observable();



    @Override
    public void registerObserver(final IObserver observer) {
        observable.registerObserver(observer);
    }

    @Override
    public void removeObserver(final IObserver observer) {
        observable.removeObserver(observer);
    }

    @Override
    public void notifyObservers() {
        observable.notifyObservers();
    }
}
