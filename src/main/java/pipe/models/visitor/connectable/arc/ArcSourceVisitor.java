package pipe.models.visitor.connectable.arc;

import pipe.models.component.Connectable;
import pipe.models.visitor.connectable.ConnectableVisitor;

/**
 * A tinytype interface to determine if the connectable is allowed to be an arc source
 */
public interface ArcSourceVisitor extends ConnectableVisitor {
    public boolean canCreate(Connectable connectable);
}
