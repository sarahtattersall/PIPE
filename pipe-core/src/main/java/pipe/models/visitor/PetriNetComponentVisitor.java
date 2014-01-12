package pipe.models.visitor;

import pipe.models.component.*;

public interface PetriNetComponentVisitor {
    public <T extends Connectable, S extends Connectable>  void visit(Arc<S, T> arc);
    public void visit(Place place);
    public void visit(Transition transition);
    public void visit(Token token);
    public void visit(Annotation annotation);
}
