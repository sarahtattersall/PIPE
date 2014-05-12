package utils;


import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.io.PetriNetReader;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.xml.bind.JAXBException;

public class Utils {
    private Utils() {
    }

    public static PetriNet readPetriNet(String path) throws JAXBException, UnparsableException {
        PetriNetReader io = new PetriNetIOImpl();
        return io.read(fileLocation(path));
    }

    public static String fileLocation(String path) {
        return Utils.class.getResource(path).getPath();
    }
}
