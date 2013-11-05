package pipe.models.visitor;

import pipe.common.dataLayer.StateGroup;
import pipe.models.*;
import pipe.views.viewComponents.RateParameter;

public class PetriNetComponentRemovalVisitor implements PetriNetComponentVisitor {
    private final PetriNet net;

    public PetriNetComponentRemovalVisitor(PetriNet net) {
        this.net = net;
    }

    @Override
    public void visit(NormalArc arc) {
        net.removeArc(arc);
    }

    @Override
    public void visit(InhibitorArc arc) {
        net.removeArc(arc);
    }

    @Override
    public void visit(Place place) {
        net.removePlace(place);
    }

    @Override
    public void visit(Transition transition) {
       net.removeTransition(transition);
    }

    @Override
    public void visit(Token token) {
       net.removeToken(token);
    }

    @Override
    public void visit(RateParameter parameter) {
        net.removeRateParameter(parameter);
    }

    @Override
    public void visit(StateGroup group) {
        net.removeStateGroup(group);
    }

    @Override
    public void visit(Annotation annotation) {
        net.removeAnnotaiton(annotation);
    }
}
