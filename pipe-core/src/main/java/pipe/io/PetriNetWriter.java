package pipe.io;

import pipe.models.PetriNet;

public interface PetriNetWriter {

    public void writeTo(String path, PetriNet petriNet);

}
