package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.models.*;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;

import java.awt.*;
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
        Place place = new Place("","");
        Transition transition = new Transition("","");
        Map<Token, String> weights = new HashMap<Token, String>();
        ArcStrategy strategy = mock(ArcStrategy.class);
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, strategy);
        arc.accept(visitor);
        verify(mockNet).removeArc(arc);
    }

    @Test
    public void testDeletesPlace() {
        Place place = new Place("","");
        place.accept(visitor);
        verify(mockNet).removePlace(place);
    }

    @Test
    public void deletesPlaceOutboundArcs() {
        Place place = new Place("","");
        Arc<Place, Transition> arc = mock(Arc.class);
        place.addOutbound(arc);

        place.accept(visitor);
        verify(mockNet).removeArc(arc);
    }

    @Test
    public void testDeletesTransition() {
        Transition transition = new Transition("","");
        transition.accept(visitor);
        verify(mockNet).removeTransition(transition);
    }


    @Test
    public void deletesTransitionOutboundArcs() {
        Transition transition = new Transition("","");
        Arc<Transition, Place> arc = mock(Arc.class);
        transition.addOutbound(arc);

        transition.accept(visitor);
        verify(mockNet).removeArc(arc);
    }

    @Test
    public void testDeletesAnnotation()  {
        Annotation annotation = new Annotation(0, 0, "", 0, 0, false);
        annotation.accept(visitor);
        verify(mockNet).removeAnnotaiton(annotation);

    }



    //TODO: CHange RateParameter to model then test this
//    @Test
//    public void testDeletesRateParameter() throws Exception {
//        RateParameter parameter = new RateParameter("", 0, 0, 0);
//        parameter.accept
//
//    }

    @Test
    public void testDeletesToken() {
        Token token = new Token("", false, 0, new Color(0, 0, 0));
        token.accept(visitor);
        verify(mockNet).removeToken(token);
    }

//    TODO: MAKE STATEGROUP HAVE VISITOR PATTERN
//    @Test
//    public void testRemoveStateGroup() throws Exception {
//
//    }
}
