package pipe.io;

import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;

public interface PetriNetReader {

    PetriNet read(String path) throws UnparsableException;
}
