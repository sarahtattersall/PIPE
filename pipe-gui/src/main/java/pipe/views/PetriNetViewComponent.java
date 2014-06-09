package pipe.views;

import java.awt.Container;

/**
 * Interface for all Petri net view components
 */
public interface PetriNetViewComponent {
    /**
     * Delete the petri net view component
     */
    void delete();

    /**
     * Each subclass should know how to add itself to a PetriNetTab
     * @param container to add itself to
     */
    void addToContainer(Container container);


}
