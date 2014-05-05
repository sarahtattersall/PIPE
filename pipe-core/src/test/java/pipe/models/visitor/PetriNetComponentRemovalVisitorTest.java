package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.exceptions.PetriNetComponentException;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.*;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.models.petrinet.PetriNetComponentRemovalVisitor;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PetriNetComponentRemovalVisitorTest {
    PetriNet mockNet;

    PetriNetComponentRemovalVisitor visitor;

    @Before
    public void setUp() {
        mockNet = mock(PetriNet.class);
        visitor = new PetriNetComponentRemovalVisitor(mockNet);
    }

    @Test
    public void testDeletesInboundNormalArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        Map<Token, String> weights = new HashMap<>();
        InboundArc arc = new InboundNormalArc(place, transition, weights);
        arc.accept(visitor);
        verify(mockNet).removeArc(arc);
    }


    @Test
    public void testDeletesOutboundNormalArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        Map<Token, String> weights = new HashMap<>();
        OutboundArc arc = new OutboundNormalArc(transition, place, weights);
        arc.accept(visitor);
        verify(mockNet).removeArc(arc);
    }


    @Test
    public void testDeletesInboundInhibitorArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        InboundArc arc = new InboundInhibitorArc(place, transition);
        arc.accept(visitor);
        verify(mockNet).removeArc(arc);
    }

    @Test
    public void testDeletesPlace() {
        Place place = new Place("", "");
        place.accept(visitor);
        verify(mockNet).removePlace(place);
    }

    @Test
    public void testDeletesTransition() {
        Transition transition = new Transition("", "");
        transition.accept(visitor);
        verify(mockNet).removeTransition(transition);
    }

    @Test
    public void testDeletesAnnotation() {
        Annotation annotation = new Annotation(0, 0, "", 0, 0, false);
        annotation.accept(visitor);
        verify(mockNet).removeAnnotaiton(annotation);

    }


    @Test
    public void testDeletesRateParameter() throws Exception {
        RateParameter parameter = new RateParameter("2", "Foo", "Foo");
        parameter.accept(visitor);
        verify(mockNet).removeRateParameter(parameter);
    }

    @Test
    public void testDeletesToken() throws PetriNetComponentException {
        Token token = new Token("", new Color(0, 0, 0));
        token.accept(visitor);
        verify(mockNet).removeToken(token);
    }
}
