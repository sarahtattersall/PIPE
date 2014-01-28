package pipe.models.component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Abstract PetriNetComponent that supports Property Changes.
 */
public abstract class AbstractPetriNetComponent implements PetriNetComponent {

    /**
     * Message fired with the id field is set
     */
    public static final String ID_CHANGE_MESSAGE = "id";

    /**
     * Message fired when the name field is set
     */
    public static final String NAME_CHANGE_MESSAGE = "name";

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

}
