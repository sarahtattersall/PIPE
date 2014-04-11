package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
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
    public void testDeletesArc() {
        Place place = new Place("", "");
        Transition transition = new Transition("", "");
        Map<Token, String> weights = new HashMap<Token, String>();
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, ArcType.NORMAL);
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
    public void testDeletesToken() {
        Token token = new Token("", new Color(0, 0, 0));
        token.accept(visitor);
        verify(mockNet).removeToken(token);
    }

    //    TODO: MAKE STATEGROUP HAVE VISITOR PATTERN
    //    @Test
    //    public void testRemoveStateGroup() throws Exception {
    //
    //    }
}
