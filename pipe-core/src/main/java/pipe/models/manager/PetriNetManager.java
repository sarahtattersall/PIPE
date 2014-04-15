package pipe.models.manager;

import java.beans.PropertyChangeListener;

/**
 * Reponsible for creating and managing Petri nets
 * Stores them in a container
 */
public interface PetriNetManager {

    /**
     * Creates a new Petri net and stores it for retrieval
     */
    public void createNewPetriNet();

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

}
