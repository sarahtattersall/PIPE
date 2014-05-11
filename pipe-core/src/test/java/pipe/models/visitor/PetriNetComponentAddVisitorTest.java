package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.exceptions.PetriNetComponentException;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.*;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.models.petrinet.PetriNetComponentAddVisitor;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PetriNetComponentAddVisitorTest {

    PetriNet mockNet;

    PetriNetComponentAddVisitor visitor;

    @Before
    public void setUp() {
        mockNet = mock(PetriNet.class);
        visitor = new PetriNetComponentAddVisitor(mockNet);
    }

    @Test
    public void testAddsInboundNormalArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        Map<String, String> weights = new HashMap<>();
        InboundArc arc = new InboundNormalArc(place, transition, weights);
        arc.accept(visitor);
        verify(mockNet).addArc(arc);

    }


    @Test
    public void testAddsOutboundNormalArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        Map<String, String> weights = new HashMap<>();
        OutboundArc arc = new OutboundNormalArc(transition, place, weights);
        arc.accept(visitor);
        verify(mockNet).addArc(arc);
    }

    @Test
    public void testAddsInhibitorArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        InboundArc arc = new InboundInhibitorArc(place, transition);
        arc.accept(visitor);
        verify(mockNet).addArc(arc);
    }

    @Test
    public void testAddsPlace() {
        Place place = new Place("", "");
        place.accept(visitor);
        verify(mockNet).addPlace(place);
    }

    @Test
    public void testAddsTransition() {
        Transition transition = new Transition("", "");
        transition.accept(visitor);
        verify(mockNet).addTransition(transition);
    }

    @Test
    public void testAddAnnotation() {
        Annotation annotation = new Annotation(0, 0, "", 0, 0, false);
        annotation.accept(visitor);
        verify(mockNet).addAnnotation(annotation);

    }

    @Test
    public void testAddsToken() throws PetriNetComponentException {
        Token token = new Token("", new Color(0, 0, 0));
        token.accept(visitor);
        verify(mockNet).addToken(token);
    }


}
