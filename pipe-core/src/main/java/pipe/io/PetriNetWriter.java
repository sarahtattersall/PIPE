package pipe.io;

import pipe.models.petrinet.PetriNet;

public interface PetriNetWriter {

    void writeTo(String path, PetriNet petriNet);

}
