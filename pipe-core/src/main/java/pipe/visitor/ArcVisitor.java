package pipe.visitor;

import pipe.models.component.Arc;
import pipe.models.component.Connectable;

public interface ArcVisitor extends PetriNetComponentVisitor {
    public <T extends Connectable, S extends Connectable>  void visit(Arc<S, T> arc);
}
