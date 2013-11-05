package pipe.models.visitor;

import pipe.common.dataLayer.StateGroup;
import pipe.models.*;
import pipe.views.viewComponents.RateParameter;

public interface PetriNetComponentVisitor {
    public void visit(NormalArc arc);
    public void visit(InhibitorArc arc);
    public void visit(Place place);
    public void visit(Transition transition);
    public void visit(Token token);
    public void visit(RateParameter parameter);
    public void visit(StateGroup group);
    public void visit(Annotation annotation);
}
