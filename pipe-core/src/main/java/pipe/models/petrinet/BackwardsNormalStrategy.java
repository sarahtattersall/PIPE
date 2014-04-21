package pipe.models.petrinet;

import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;


/**
 * Backwards strategy is for normal arcs that map Places -> Transitions
 */
public class BackwardsNormalStrategy implements ArcStrategy<Place, Transition> {


    @Override
    public boolean canFire(PetriNet petriNet, Arc<Place, Transition> arc) {
        Place place = arc.getSource();
        for (Token token : arc.getTokenWeights().keySet()) {
            int tokenCount = place.getTokenCount(token);
            IncidenceMatrix backwardsIncidenceMatrix = petriNet.getBackwardsIncidenceMatrix(token);

            if (tokenCount < backwardsIncidenceMatrix.get(place, arc.getTarget()) && tokenCount != -1) {
                return false;
            }
        }
        return !arc.getSource().getTokenCounts().isEmpty();
    }

}
