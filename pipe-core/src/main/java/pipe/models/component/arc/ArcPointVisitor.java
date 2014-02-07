package pipe.models.component.arc;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface ArcPointVisitor extends PetriNetComponentVisitor {
    void visit(ArcPoint arcPoint);
}
