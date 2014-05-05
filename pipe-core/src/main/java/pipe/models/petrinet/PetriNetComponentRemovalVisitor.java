package pipe.models.petrinet;

import pipe.exceptions.InvalidRateException;
import pipe.exceptions.PetriNetComponentException;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.annotation.AnnotationVisitor;
import pipe.models.component.arc.ArcVisitor;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.arc.OutboundArc;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.rate.RateParameterVisitor;
import pipe.models.component.token.Token;
import pipe.models.component.token.TokenVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;

public class PetriNetComponentRemovalVisitor
        implements PlaceVisitor, TransitionVisitor, ArcVisitor, TokenVisitor, AnnotationVisitor, RateParameterVisitor {
    private final PetriNet net;

    public PetriNetComponentRemovalVisitor(PetriNet net) {
        this.net = net;
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
    public void visit(Token token) throws PetriNetComponentException {
        net.removeToken(token);
    }

    @Override
    public void visit(Annotation annotation) {
        net.removeAnnotaiton(annotation);
    }

    @Override
    public void visit(RateParameter rate) throws InvalidRateException {
        net.removeRateParameter(rate);
    }

    @Override
    public void visit(InboundArc inboundArc) {
        net.removeArc(inboundArc);
    }

    @Override
    public void visit(OutboundArc outboundArc) {
        net.removeArc(outboundArc);
    }
}
