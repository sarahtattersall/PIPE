package pipe.naming;

import pipe.models.petrinet.PetriNet;

/**
 * This class provides names for petri nets by registering them with this class
 */
public class PetriNetNamer extends AbstractUniqueNamer {

    public PetriNetNamer() {
        super("Petri Net ");
    }

    /**
     *
     * Registers the petri net name in the system
     *
     * @param petriNet new petri net
     */
    public void registerPetriNet(PetriNet petriNet) {
        names.add(petriNet.getNameValue());
    }

    /**
     *
     * Removes the petri net name from the system
     *
     * @param petriNet existing petri net whose name can be reused
     */
    public void deRegisterPetriNet(PetriNet petriNet) {
        names.remove(petriNet.getNameValue());
    }
}
