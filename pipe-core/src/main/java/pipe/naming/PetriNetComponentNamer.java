package pipe.naming;

import pipe.models.petrinet.PetriNet;

/**
 * Gives unique names to places, transitions and arcs
 */
public class PetriNetComponentNamer implements MultipleNamer {

    private final UniqueNamer placeNamer;
    private final UniqueNamer transitionNamer;

    public PetriNetComponentNamer(PetriNet net) {
        placeNamer = new PlaceNamer(net);
        transitionNamer = new TransitionNamer(net);
    }


    @Override
    public String getPlaceName() {
        return placeNamer.getName();
    }

    @Override
    public String getTransitionName() {
        return transitionNamer.getName();
    }

    //TODO:
    @Override
    public String getArcName() {
        return "";
    }
}
