package pipe.io;

import pipe.models.PetriNet;

public interface PetriNetReader {

    PetriNet read(String path);
}
