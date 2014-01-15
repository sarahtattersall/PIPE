package pipe.models.strategy.arc;

import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.math.IncidenceMatrix;

/**
 * Backwards strategy is for Places -> Transitions
 *
 * Currently for io implementation we have to set the PetriNet after this has been created
 * Unfortunately due to the way jaxb works we don't have a blank petri net
 * to create this with at the time. This is why we have a setter
 */
public class BackwardsNormalStrategy implements ArcStrategy<Place, Transition> {
    private PetriNet petriNet;

    public void setPetriNet(PetriNet petriNet) {
        this.petriNet = petriNet;
    }



    @Override
    public boolean canFire(final Arc<Place, Transition> arc) {
        if (petriNet == null) {
            throw new RuntimeException("Error petri net not initialised in BackwardsNormalStrategy");
        }
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
