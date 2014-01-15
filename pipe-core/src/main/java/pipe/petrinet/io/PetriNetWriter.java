package pipe.petrinet.io;

import pipe.models.PetriNet;

import javax.xml.bind.JAXBException;

public interface PetriNetWriter {

    public void writeTo(String path, PetriNet petriNet);

}
