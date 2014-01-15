package pipe.petrinet.io;

import pipe.models.PetriNet;

public interface PetriNetReader {

    PetriNet read(String path);
}
