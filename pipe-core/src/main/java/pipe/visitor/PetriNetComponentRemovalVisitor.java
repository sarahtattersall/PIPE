package pipe.visitor;

import pipe.models.PetriNet;
import pipe.models.component.*;

public class PetriNetComponentRemovalVisitor implements PetriNetComponentVisitor {
    private final PetriNet net;

    public PetriNetComponentRemovalVisitor(PetriNet net) {
        this.net = net;
    }

    @Override
    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
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

    //    @Override
    //    public void visit(RateParameter parameter) {
    //        net.removeRateParameter(parameter);
    //    }
    //
    //    @Override
    //    public void visit(StateGroup group) {
    //        net.removeStateGroup(group);
    //    }

    @Override
    public void visit(Annotation annotation) {
        net.removeAnnotaiton(annotation);
    }

    @Override
    public void visit(ArcPoint arcPoint) {

    }
}
