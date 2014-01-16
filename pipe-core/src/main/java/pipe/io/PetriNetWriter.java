package pipe.io;

import pipe.models.petrinet.PetriNet;

public interface PetriNetWriter {

    public void writeTo(String path, PetriNet petriNet);

}
