package pipe.models.petrinet;

import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;

/**
 * Arc strategy used to determine arc behaviour
 * It's package only since it's only meant to be used in the petri net to fire transitions
 */
interface ArcStrategy<S extends Connectable, T extends Connectable> {
    public boolean canFire(PetriNet petriNet, Arc<S, T> arc);
}
