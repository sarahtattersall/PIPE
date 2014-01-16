package pipe.visitor;

import pipe.models.component.Transition;

public interface TransitionVisitor extends PetriNetComponentVisitor {
    public void visit(Transition transition);
}
