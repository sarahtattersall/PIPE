package pipe.models.component.arc;

import pipe.models.component.Connectable;
import pipe.visitor.component.PetriNetComponentVisitor;

public interface ArcVisitor extends PetriNetComponentVisitor {
    <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc);
}
