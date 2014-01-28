package pipe.models.component.arc;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface ArcPointVisitor extends PetriNetComponentVisitor {
    void visit(ArcPoint arcPoint);
}
