package pipe.models.component.arc;

import pipe.models.component.Connectable;
import pipe.visitor.foo.PetriNetComponentVisitor;

public interface ArcVisitor extends PetriNetComponentVisitor {
    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc);
}
