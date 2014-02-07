package pipe.models.component.rate;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface RateVisitor extends PetriNetComponentVisitor {
    void visit(Rate rate);
}