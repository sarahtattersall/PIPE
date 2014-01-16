package pipe.models.component.arc;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface ArcPointVisitor extends PetriNetComponentVisitor {
    public void visit(ArcPoint arcPoint);
}
