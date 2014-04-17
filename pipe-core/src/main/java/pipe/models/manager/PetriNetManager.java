package pipe.models.manager;

import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;

import javax.xml.bind.JAXBException;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Responsible for creating and managing Petri nets
 * It stores the nets it creates for easy retrial and can notify
 * listeners on changes to its structure
 */
public interface PetriNetManager {

    /**
     * Creates a new Petri net and stores it for retrieval later
     */
    void createNewPetriNet();

    /**
     * Registers a listener for petri net change events
     * @param listener notify this listener on any changes
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a listener, after this call it will no longer be called
     * on change events
     * @param listener registered listener that no longer wishes to be notified
     *                 on change
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns the last Petri net that it holds, this will be the most recently created
     * Petri net (that has not been deleted)
     */
    PetriNet getLastNet();

    /**
     * Creates Petri net by reading in and parsing the contents of the file
     * @param file location of Petri net xml file
     */
    void createFromFile(File file) throws JAXBException, UnparsableException;

    /**
     *
     * Saves the specified petri net to the location
     *
     * @param petriNet petri net to save
     * @param outFile file to save petri net to
     */
    //TODO: SHOULD REALLY TELL IT TO SAVE ONE OF ITS OWN PETRI NETS RAHTER THAN PASSING IT IN
    void savePetriNet(PetriNet petriNet, File outFile) throws JAXBException;

    /**
     * Remove this Petri net from storage
     * @param petriNet
     */
    void remove(PetriNet petriNet);
}
