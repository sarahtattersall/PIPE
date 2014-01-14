package pipe.visitor.connectable.arc;

import pipe.models.component.Connectable;
import pipe.visitor.connectable.ConnectableVisitor;

/**
 * A tinytype interface to determine if the connectable is allowed to be an arc source
 */
public interface ArcSourceVisitor extends ConnectableVisitor {
    /**
     *
     * @param connectable parameter to try and start arc from
     * @return true if we can start the type of arc at this connectable
     */
    public boolean canStart(Connectable connectable);
}
