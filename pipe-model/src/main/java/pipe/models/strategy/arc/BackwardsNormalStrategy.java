package pipe.models.strategy.arc;

import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.utilities.math.IncidenceMatrix;

/**
 * Backwards strategy is for Places -> Transitions
 */
public class BackwardsNormalStrategy implements ArcStrategy<Place, Transition> {
    private final PetriNet petriNet;

    public BackwardsNormalStrategy(PetriNet petriNet) {
        this.petriNet = petriNet;
    }


    @Override
    public boolean canFire(final Arc<Place, Transition> arc) {
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

    @Override
    public ArcType getType() {
        return ArcType.NORMAL;
    }
}
