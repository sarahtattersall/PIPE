package pipe.models.component.transition;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface TransitionVisitor extends PetriNetComponentVisitor {
    void visit(Transition transition);
}
