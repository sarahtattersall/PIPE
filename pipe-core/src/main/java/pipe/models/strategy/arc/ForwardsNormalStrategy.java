package pipe.models.strategy.arc;

import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.math.IncidenceMatrix;

/**
 * Forwards strategy is for Transitions -> Places
 *
 * Currently have to set the petri net once it's made. This isn't the best
 * design obviously, but due to jaxb we don't have the petrinet whilst
 * it's creating the object
 */
public class ForwardsNormalStrategy implements ArcStrategy<Transition, Place> {

    private PetriNet petriNet;

    public void setPetriNet(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    @Override
    public boolean canFire(Arc<Transition, Place> arc) {
        if (petriNet == null) {
            throw new RuntimeException("Error petri net not initialised in BackwardsNormalStrategy");
        }
        Place place = arc.getTarget();
        if (place.getCapacity() == 0) { // No capacity restrictions
            return true;
        }

        int totalTokensIn = 0;
        int totalTokensOut = 0;

        Transition transition = arc.getSource();
        for (Token token : arc.getTokenWeights().keySet()) {
            IncidenceMatrix forwardsIncidenceMatrix = petriNet.getForwardsIncidenceMatrix(token);
            IncidenceMatrix backwardsIncidenceMatrix = petriNet.getBackwardsIncidenceMatrix(token);

            totalTokensIn += forwardsIncidenceMatrix.get(place, transition);
            totalTokensOut += backwardsIncidenceMatrix.get(place, transition);
        }

        return (place.getNumberOfTokensStored() + totalTokensIn - totalTokensOut <= place.getCapacity());
    }


    @Override
    public ArcType getType() {
        return ArcType.NORMAL;
    }
}
