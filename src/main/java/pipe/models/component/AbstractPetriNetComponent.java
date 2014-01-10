package pipe.models.component;

import pipe.models.Observable;
import pipe.models.interfaces.IObserver;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class implements the IObserverbale pattern and acts as a wrapper
 * for {@link pipe.models.Observable}
 */
public abstract class AbstractPetriNetComponent implements PetriNetComponent {

    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void  removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
