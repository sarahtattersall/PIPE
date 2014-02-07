package pipe.models.component.transition;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface TransitionVisitor extends PetriNetComponentVisitor {
    void visit(Transition transition);
}
