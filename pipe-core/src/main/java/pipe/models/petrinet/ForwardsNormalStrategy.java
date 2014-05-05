package pipe.models.petrinet;

import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;


/**
 * Forward strategy is for normal arcs that map Transitions -> Places
 */
public class ForwardsNormalStrategy implements ArcStrategy<Transition, Place> {

    @Override
    public boolean canFire(PetriNet petriNet, Arc<Transition, Place> arc) {
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
}
