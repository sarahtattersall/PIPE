package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.arc.ArcType;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNetComponentAddVisitor;

import java.awt.*;
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
    public void testAddsArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        Map<Token, String> weights = new HashMap<Token, String>();
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, ArcType.NORMAL);
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

    //TODO: CHange RateParameter to model then test this
    //    @Test
    //    public void testAddsRateParameter() throws Exception {
    //        RateParameter parameter = new RateParameter("", 0, 0, 0);
    //        parameter.accept
    //
    //    }

    @Test
    public void testAddsToken() {
        Token token = new Token("", false, 0, new Color(0, 0, 0));
        token.accept(visitor);
        verify(mockNet).addToken(token);
    }

    //    TODO: MAKE STATEGROUP HAVE VISITOR PATTERN
    //    @Test
    //    public void testAddsStateGroup() throws Exception {
    //
    //    }

}
