package pipe.naming;

import pipe.models.petrinet.PetriNet;

/**
 * Gives unique names to places, transitions and arcs
 */
public class UniqueNamer implements MultipleNamer {

    private final PetriNetComponentNamer placeNamer;
    private final PetriNetComponentNamer transitionNamer;

    public UniqueNamer(PetriNet net) {
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
