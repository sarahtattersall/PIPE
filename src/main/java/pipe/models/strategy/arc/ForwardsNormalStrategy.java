package pipe.models.strategy.arc;

import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.utilities.math.IncidenceMatrix;

/**
 * Forwards strategy is for Transitions -> Places
 */
public class ForwardsNormalStrategy implements ArcStrategy<Transition, Place<Transition>> {

    private final PetriNet petriNet;

    public ForwardsNormalStrategy(final PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    @Override
    public boolean canFire(Arc<Transition, Place<Transition>> arc) {
        Place<Transition> place = arc.getTarget();
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
