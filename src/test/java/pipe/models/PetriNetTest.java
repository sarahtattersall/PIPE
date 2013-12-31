package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.common.dataLayer.StateGroup;
import pipe.models.component.*;
import pipe.models.interfaces.IObserver;
import pipe.utilities.math.IncidenceMatrix;
import pipe.views.viewComponents.RateParameter;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PetriNetTest {
    PetriNet net;
    IObserver mockObserver;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        net = new PetriNet();
        mockObserver = mock(IObserver.class);
    }

    @Test
    public void addingPlaceNotifiesObservers() {
        net.registerObserver(mockObserver);
        Place place = new Place("", "");
        net.addPlace(place);

        verify(mockObserver).update();
    }

    @Test
    public void removingPlaceNotifiesObservers() {
        net.registerObserver(mockObserver);
        Place place = new Place("", "");
        net.addPlace(place);
        net.removePlace(place);

        verify(mockObserver, times(2)).update();
    }

    @Test
    public void addingArcNotifiesObservers() {
        net.registerObserver(mockObserver);
        Arc mockArc = mock(Arc.class);
        net.addArc(mockArc);

        verify(mockObserver).update();
    }

    @Test
    public void removingArcNotifiesObservers() {
        net.registerObserver(mockObserver);
        Arc mockArc = mock(Arc.class);
        Connectable connectable = mock(Connectable.class);
        when(mockArc.getTarget()).thenReturn(connectable);
        when(mockArc.getSource()).thenReturn(connectable);
        net.addArc(mockArc);
        net.removeArc(mockArc);

        verify(mockObserver, times(2)).update();
    }

    @Test
    public void removesArcFromSource() throws Exception {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        Map<Token, String> weights = new HashMap<Token, String>();
        NormalArc arc = new NormalArc(place, transition, weights);
        net.addArc(arc);
        net.removeArc(arc);
        verify(place).removeOutboundArc(arc);
    }

    @Test
    public void removesArcFromTarget() throws Exception {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        Map<Token, String> weights = new HashMap<Token, String>();
        NormalArc arc = new NormalArc(place, transition, weights);
        net.addArc(arc);
        net.removeArc(arc);
        verify(transition).removeInboundArc(arc);
    }

    @Test
    public void addingTransitionNotifiesObservers() {
        net.registerObserver(mockObserver);
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        verify(mockObserver).update();
    }


    @Test
    public void removingTransitionNotifiesObservers() {
        net.registerObserver(mockObserver);
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        net.removeTransition(transition);
        verify(mockObserver, times(2)).update();
    }


    @Test
    public void addingAnnotationNotifiesObservers() {
        net.registerObserver(mockObserver);
        Annotation annotation = new Annotation(10, 10, "", 10, 10, false);
        net.addAnnotaiton(annotation);
        verify(mockObserver).update();
    }

    @Test
    public void addingRateParameterNotifiesObservers() {

        net.registerObserver(mockObserver);
        RateParameter rateParameter = new RateParameter("", 0., 0, 0);
        net.addRate(rateParameter);
        verify(mockObserver).update();
    }


    @Test
    public void addingTokenNotifiesObservers() {

        net.registerObserver(mockObserver);
        Token token = new Token();
        net.addToken(token);
        verify(mockObserver).update();
    }

    @Test
    public void addingStateGroupNotifiesObservers() {

        net.registerObserver(mockObserver);
        StateGroup group = new StateGroup();
        net.addStateGroup(group);
        verify(mockObserver).update();
    }

    @Test
    public void genericRemoveMethodRemovesPlace() {
        Place place = new Place("", "");
        net.addPlace(place);

        assertEquals(1, net.getPlaces().size());
        net.remove(place);
        assertTrue(net.getPlaces().isEmpty());
    }

    @Test
    public void genericRemoveMethodRemovesArc() {
        Place place = new Place("source", "source");
        Transition transition = new Transition("target", "target");
        Map<Token, String> weights = new HashMap<Token, String>();
        NormalArc arc = new NormalArc(place, transition, weights);
        net.addArc(arc);

        assertEquals(1, net.getArcs().size());
        net.remove(arc);
        assertTrue(net.getArcs().isEmpty());
    }

    @Test
    public void returnsCorrectToken() {
        String id = "Token1";
        boolean enabled = true;
        Color color = new Color(132, 16, 130);
        Token token = new Token(id, enabled, 0, color);
        net.addToken(token);
        assertEquals(token, net.getToken(id));
    }

    @Test
    public void throwsErrorIfNoTokenExists() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("No token foo exists in petrinet.");
        net.getToken("foo");
    }

    @Test
    public void registersItselfAsPlaceObserver() {
        Place place = mock(Place.class);
        net.addPlace(place);
        verify(place).registerObserver(net);
    }


    @Test
    public void registersItselfAsTransitionObserver() {
        Transition transition = mock(Transition.class);
        net.addTransition(transition);
        verify(transition).registerObserver(net);
    }


    @Test
    public void registersItselfAsArcObserver() {
        Arc arc = mock(Arc.class);
        net.addArc(arc);
        verify(arc).registerObserver(net);
    }


    @Test
    public void correctForwardIncidenceMatrix() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        IncidenceMatrix forwardMatrix = container.petriNet.getForwardsIncidenceMatrix(container.token);
        assertEquals(tokenWeight, forwardMatrix.get(container.place2, container.transition));
        assertEquals(0, forwardMatrix.get(container.place, container.transition));
    }


    @Test
    public void correctBackwardIncidenceMatrix() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        IncidenceMatrix backwardIncidence = container.petriNet.getBackwardsIncidenceMatrix(container.token);
        assertEquals(tokenWeight, backwardIncidence.get(container.place, container.transition));
        assertEquals(0, backwardIncidence.get(container.place2, container.transition));
    }



    @Test
    public void correctlyIdentifiesEnabledTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(container.transition));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToEmptyPlace() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.place.decrementTokenCount(container.token);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transition));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToNotEnoughTokens() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transition));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToOnePlaceNotEnoughTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNetTwoPlacesToTransition(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transition));
    }


    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToArcNeedingTwoDifferentTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arc.getTokenWeights().put(redToken, "1");

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transition));
    }

    @Test
    public void correctlyIdentifiesEnabledTransitionRequiringTwoTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arc.getTokenWeights().put(redToken, "1");
        container.place.incrementTokenCount(redToken);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(container.transition));
    }

    @Test
    public void correctlyMarksEnabledTransitions() {
        PetriNetContainer container = createSimplePetriNet(1);
        container.petriNet.markEnabledTransitions();
        assertTrue("Did not enable transition", container.transition.isEnabled());
    }

    @Test
    public void correctlyDoesNotMarkNotEnabledTransitions() {
        PetriNetContainer container = createSimplePetriNet(2);
        container.petriNet.markEnabledTransitions();
        assertFalse("Enabled transition when it cannot fire", container.transition.isEnabled());
    }

    @Test
    public void firingTransitionMovesToken() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);


        container.petriNet.markEnabledTransitions();
        container.petriNet.fireTransition(container.transition);

        assertEquals(0, container.place.getTokenCount(container.token));
        assertEquals(1, container.place2.getTokenCount(container.token));
    }


    @Test
    public void firingTransitionDisablesTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        container.petriNet.markEnabledTransitions();
        container.petriNet.fireTransition(container.transition);

        assertFalse("Transition was not disabled", container.transition.isEnabled());
    }

    @Test
    public void firingTransitionDoesNotDisableTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.place.setTokenCount(container.token, 2);

        container.petriNet.fireTransition(container.transition);

        assertTrue("Transition was disabled when it could have fired again", container.transition.isEnabled());
    }


    @Test
    public void firingTransitionEnablesNextTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        Transition transition = new Transition("t2", "t2");
        Arc arc3 = new NormalArc(container.place2, transition, container.arc.getTokenWeights());
        container.petriNet.addArc(arc3);
        container.petriNet.addTransition(transition);

        container.place.setTokenCount(container.token, 1);

        container.petriNet.markEnabledTransitions();
        container.petriNet.fireTransition(container.transition);

        assertTrue("Next transition was enabled", transition.isEnabled());
    }

    @Test
    public void firingTransitionBackwardMovesTokensBack() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.place.setTokenCount(container.token, 0);
        container.place2.setTokenCount(container.token, 1);

        container.petriNet.fireTransitionBackwards(container.transition);

        assertEquals(0, container.place2.getTokenCount(container.token));
        assertEquals(1, container.place.getTokenCount(container.token));
    }

    @Test
    public void firingTransitionBackwardEnablesTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.place.setTokenCount(container.token, 0);
        container.place2.setTokenCount(container.token, 1);

        container.petriNet.fireTransitionBackwards(container.transition);
        assertTrue("Transition was not enabled", container.transition.isEnabled());
    }


    /**
     * Create simple petrinet with P1 -> T1 -> P2
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNetContainer createSimplePetriNet(int tokenWeight) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("p1", "p1");
        Transition transition = new Transition("t1", "t1");

        Map<Token, String> arcWeight = new HashMap<Token, String>();
        arcWeight.put(token, Integer.toString(tokenWeight));

        Arc arc = new NormalArc(place, transition, arcWeight);
        Place place2 = new Place("p2", "p2");
        Arc arc2 = new NormalArc(transition, place2, arcWeight);

        PetriNet petriNet = new PetriNet();
        petriNet.addToken(token);
        petriNet.addPlace(place);
        petriNet.addPlace(place2);
        petriNet.addTransition(transition);
        petriNet.addArc(arc);
        petriNet.addArc(arc2);

        place.incrementTokenCount(token);

        return new PetriNetContainer(token, place, place2, transition, arc, arc2, petriNet);
    }

    /**
     * Create simple petrinet with P1 -> T1 and P2 -> T1
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNetContainer createSimplePetriNetTwoPlacesToTransition(int tokenWeight) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("p1", "p1");
        Transition transition = new Transition("t1", "t1");

        Map<Token, String> arcWeight = new HashMap<Token, String>();
        arcWeight.put(token, Integer.toString(tokenWeight));

        Arc arc = new NormalArc(place, transition, arcWeight);
        Place place2 = new Place("p2", "p2");
        Arc arc2 = new NormalArc(place2, transition, arcWeight);

        PetriNet petriNet = new PetriNet();
        petriNet.addToken(token);
        petriNet.addPlace(place);
        petriNet.addPlace(place2);
        petriNet.addTransition(transition);
        petriNet.addArc(arc);
        petriNet.addArc(arc2);

        place.incrementTokenCount(token);

        return new PetriNetContainer(token, place, place2, transition, arc, arc2, petriNet);
    }

    private class PetriNetContainer {
        private PetriNetContainer(final Token token, final Place place, final Place place2, final Transition transition,
                                  final Arc arc, final Arc arc2, final PetriNet petriNet) {
            this.token = token;
            this.place = place;
            this.place2 = place2;
            this.transition = transition;
            this.arc = arc;
            this.arc2 = arc2;
            this.petriNet = petriNet;
        }

        public final Token token;
        public final Place place;
        public final Place place2;
        public final Transition transition;
        public final Arc arc;
        public final Arc arc2;
        public final PetriNet petriNet;


    }
}
