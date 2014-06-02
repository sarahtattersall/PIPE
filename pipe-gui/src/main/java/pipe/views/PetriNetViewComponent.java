package pipe.views;

import java.awt.Container;

public interface PetriNetViewComponent {
    void delete();

    /**
     * Each subclass should know how to add itself to a PetriNetTab
     * @param container to add itself to
     */
    void addToContainer(Container container);


}
