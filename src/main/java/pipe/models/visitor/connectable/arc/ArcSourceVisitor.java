package pipe.models.visitor.connectable.arc;

import pipe.models.component.Connectable;
import pipe.models.visitor.connectable.ConnectableVisitor;

/**
 * A tinytype interface to determine if the connectable is allowed to be an arc source
 */
public interface ArcSourceVisitor extends ConnectableVisitor {
    /**
     *
     * @param connectable parameter to try and connect arc to
     * @param <S> source type
     * @param <T> target type
     * @return true if we can connect connectable here
     */
    public <S extends Connectable<T, S>, T extends Connectable<S, T>> boolean canCreate(Connectable<S,T> connectable);
}
