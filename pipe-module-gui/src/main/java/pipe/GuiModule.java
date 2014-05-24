package pipe;

import uk.ac.imperial.pipe.models.petrinet.PetriNet;

/**
 * API for GUI modules
 */
public interface GuiModule {

    /**
     * Start a module using optionally the current Petri net
     * @param petriNet
     */
    void start(PetriNet petriNet);
}
