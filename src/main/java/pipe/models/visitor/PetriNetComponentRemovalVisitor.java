package pipe.models.visitor;

import pipe.common.dataLayer.StateGroup;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.views.viewComponents.RateParameter;

public class PetriNetComponentRemovalVisitor implements PetriNetComponentVisitor {
    private final PetriNet net;

    public PetriNetComponentRemovalVisitor(PetriNet net) {
        this.net = net;
    }

    @Override
    public <S extends Connectable, T extends Connectable> void visit(Arc<S,T> arc) {
        net.removeArc(arc);
    }

    @Override
    public <T extends Connectable> void visit(Place place) {
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
