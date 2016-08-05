package pipe.gui.plugin;

import uk.ac.imperial.pipe.models.petrinet.PetriNet;

/**
 * API for GUI modules
 */
public interface GuiModule {

    /**
     * Start a module using optionally the current Petri net
     * @param petriNet to start
     */
    void start(PetriNet petriNet);

    /**
     *
     * @return the name of the module for
     */
    String getName();
}
