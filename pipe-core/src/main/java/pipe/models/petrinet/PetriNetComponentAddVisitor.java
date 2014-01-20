package pipe.models.petrinet;

import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.annotation.AnnotationVisitor;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcVisitor;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.token.Token;
import pipe.models.component.token.TokenVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;

public class PetriNetComponentAddVisitor
        implements PlaceVisitor, ArcVisitor, TransitionVisitor, TokenVisitor, AnnotationVisitor {
    private final PetriNet petriNet;

    public PetriNetComponentAddVisitor(PetriNet petriNet) {
        this.petriNet = petriNet;
    }


    @Override
    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
        petriNet.addArc(arc);
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
        petriNet.addAnnotaiton(annotation);
    }
}
