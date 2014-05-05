package pipe.models.petrinet;

import pipe.exceptions.InvalidRateException;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.annotation.AnnotationVisitor;
import pipe.models.component.arc.Arc;
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

public class PetriNetComponentAddVisitor
        implements PlaceVisitor, ArcVisitor, TransitionVisitor, TokenVisitor, AnnotationVisitor, RateParameterVisitor {
    private final PetriNet petriNet;

    public PetriNetComponentAddVisitor(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    @Override
    public void visit(Place place) {
        petriNet.addPlace(place);
    }

    @Override
    public void visit(Transition transition) {
        petriNet.addTransition(transition);
    }

    @Override
    public void visit(Token token) {
        petriNet.addToken(token);
    }

    @Override
    public void visit(Annotation annotation) {
        petriNet.addAnnotation(annotation);
    }

    @Override
    public void visit(RateParameter rate) throws InvalidRateException {
        petriNet.addRateParameter(rate);
    }

    @Override
    public void visit(InboundArc inboundArc) {
        petriNet.addArc(inboundArc);
    }

    @Override
    public void visit(OutboundArc outboundArc) {
        petriNet.addArc(outboundArc);
    }
}
