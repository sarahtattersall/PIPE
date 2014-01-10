package pipe.models.strategy.arc;

import pipe.models.component.*;
import pipe.utilities.math.IncidenceMatrix;

/**
 * Arc strategy used to determine arc behaviour
 */
public interface ArcStrategy<S extends Connectable<T,S>, T extends Connectable<S,T>> {
    public boolean canFire(Arc<S, T> arc);

    ArcType getType();
}
