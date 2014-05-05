package pipe.models.petrinet;

import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

/**
 * Inhibitor strategy is for inhibitor arcs that map place -> transition
 */
public class InhibitorStrategy implements ArcStrategy<Place, Transition> {
    @Override
    public boolean canFire(PetriNet petriNet, Arc<Place, Transition> arc) {
        return arc.getSource().getNumberOfTokensStored() == 0;
    }

}
