package pipe.visitor;

import pipe.models.PetriNet;
import pipe.models.component.*;

public class PetriNetComponentAddVisitor implements PlaceVisitor, ArcVisitor, TransitionVisitor, TokenVisitor, AnnotationVisitor {
    private PetriNet petriNet;

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
