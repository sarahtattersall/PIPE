package pipe.visitor;

import pipe.models.component.ArcPoint;

public interface ArcPointVisitor extends PetriNetComponentVisitor {
    public void visit(ArcPoint arcPoint);
}
