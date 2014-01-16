package pipe.models.component.transition;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface TransitionVisitor extends PetriNetComponentVisitor {
    public void visit(Transition transition);
}
