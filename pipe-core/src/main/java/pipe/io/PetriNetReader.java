package pipe.io;

import pipe.models.petrinet.PetriNet;

public interface PetriNetReader {

    PetriNet read(String path);
}
