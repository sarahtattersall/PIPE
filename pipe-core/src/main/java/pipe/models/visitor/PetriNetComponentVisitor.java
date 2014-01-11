package pipe.models.visitor;

import pipe.models.component.*;

public interface PetriNetComponentVisitor {
    public void visit(Arc<? extends Connectable, ? extends Connectable> arc);
    public void visit(Place place);
    public void visit(Transition transition);
    public void visit(Token token);
    public void visit(Annotation annotation);
}
