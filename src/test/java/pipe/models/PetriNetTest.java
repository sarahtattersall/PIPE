package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.common.dataLayer.StateGroup;
import pipe.models.component.*;
import pipe.models.interfaces.IObserver;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.models.strategy.arc.ForwardsNormalStrategy;
import pipe.models.strategy.arc.InhibitorStrategy;
import pipe.utilities.math.IncidenceMatrix;
import pipe.views.viewComponents.RateParameter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        Arc arc = new Arc<Place, Transition>(place, transition, weights, new BackwardsNormalStrategy(net));
        net.addArc(arc);
        net.removeArc(arc);
        verify(place).removeOutboundArc(arc);
    }

    @Test
    public void removesArcFromTarget() throws Exception {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        Map<Token, String> weights = new HashMap<Token, String>();
        Arc arc = new Arc<Place, Transition>(place, transition, weights, new BackwardsNormalStrategy(net));
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
        Arc arc = new Arc<Place, Transition>(place, transition, weights, new BackwardsNormalStrategy(net));
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

        IncidenceMatrix forwardMatrix = container.petriNet.getForwardsIncidenceMatrix(container.tokens.get(0));

        Transition transition = container.transitions.get(0);
        assertEquals(tokenWeight, forwardMatrix.get(container.places.get(1), transition));
        assertEquals(0, forwardMatrix.get(container.places.get(0), transition));
    }


    @Test
    public void correctBackwardIncidenceMatrix() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        IncidenceMatrix backwardIncidence = container.petriNet.getBackwardsIncidenceMatrix(container.tokens.get(0));
        Transition transition = container.transitions.get(0);
        assertEquals(tokenWeight, backwardIncidence.get(container.places.get(0), transition));
        assertEquals(0, backwardIncidence.get(container.places.get(1), transition));
    }



    @Test
    public void correctlyIdentifiesEnabledTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesEnabledWithNoSecondColourToken() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arcs.get(0).setWeight(redToken, "0");

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToEmptyPlace() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.places.get(0).decrementTokenCount(container.tokens.get(0));

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToNotEnoughTokens() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToOnePlaceNotEnoughTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNetTwoPlacesToTransition(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }


    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToArcNeedingTwoDifferentTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arcs.get(0).getTokenWeights().put(redToken, "1");

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesEnabledTransitionRequiringTwoTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arcs.get(0).getTokenWeights().put(redToken, "1");
        container.places.get(0).incrementTokenCount(redToken);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions(false);
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyMarksEnabledTransitions() {
        PetriNetContainer container = createSimplePetriNet(1);
        container.petriNet.markEnabledTransitions();
        assertTrue("Did not enable transition", container.transitions.get(0).isEnabled());
    }

    @Test
    public void correctlyDoesNotMarkNotEnabledTransitions() {
        PetriNetContainer container = createSimplePetriNet(2);
        System.out.println("PRE RUN ARCS SIZE " + container.petriNet.getArcs().size());
        container.petriNet.markEnabledTransitions();
        assertFalse("Enabled transition when it cannot fire", container.transitions.get(0).isEnabled());
    }

    @Test
    public void correctlyDoesNotMarkNotEnabledTransitionsIfPlaceCapacityIsFull() {
        PetriNetContainer container = createSimplePetriNet(2);
        container.places.get(0).setTokenCount(container.tokens.get(0), 2);
        container.places.get(1).setCapacity(1);
        container.petriNet.markEnabledTransitions();
        assertFalse("Enabled transition when it cannot fire", container.transitions.get(0).isEnabled());
    }

    @Test
    public void correctlyMarksEnabledTransitionIfSelfLoop() {
        PetriNetContainer container = createSelfLoopPetriNet(1);
        Place place = container.places.get(0);
        place.setTokenCount(container.tokens.get(0), 1);
        place.setCapacity(1);
        container.petriNet.markEnabledTransitions();
        assertTrue("Did not enable transition when it can fire", container.transitions.get(0).isEnabled());
    }

    @Test
    public void correctlyMarksInhibitorArcEnabledTransition() {
        PetriNetContainer container = createSimpleInhibitorPetriNet(1);
        container.petriNet.markEnabledTransitions();
        assertTrue("Did not enable transition when it can fire", container.transitions.get(0).isEnabled());
    }

    @Test
    public void correctlyMarksInhibitorArcEnabledTransitionEvenAfterFiring() {
        PetriNetContainer container = createSimpleInhibitorPetriNet(1);
        container.petriNet.markEnabledTransitions();
        container.petriNet.fireTransition(container.transitions.get(0));
        assertTrue("Did not enable transition when it can fire", container.transitions.get(0).isEnabled());
    }


    @Test
    public void firingTransitionMovesToken() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);


        container.petriNet.markEnabledTransitions();
        container.petriNet.fireTransition(container.transitions.get(0));

        Token token = container.tokens.get(0);
        assertEquals(0, container.places.get(0).getTokenCount(token));
        assertEquals(1, container.places.get(1).getTokenCount(token));
    }


    @Test
    public void firingTransitionDisablesTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        container.petriNet.markEnabledTransitions();
        Transition transition = container.transitions.get(0);
        container.petriNet.fireTransition(transition);

        assertFalse("Transition was not disabled", transition.isEnabled());
    }

    @Test
    public void firingTransitionDoesNotDisableTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.places.get(0).setTokenCount(container.tokens.get(0), 2);

        Transition transition = container.transitions.get(0);
        container.petriNet.fireTransition(transition);

        assertTrue("Transition was disabled when it could have fired again", transition.isEnabled());
    }


    @Test
    public void firingTransitionEnablesNextTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        Transition transition = new Transition("t2", "t2");
        Arc arc3 = new Arc<Place, Transition>(container.places.get(1), transition, container.arcs.get(0).getTokenWeights(), new BackwardsNormalStrategy(net));
        container.petriNet.addArc(arc3);
        container.petriNet.addTransition(transition);

        container.places.get(0).setTokenCount(container.tokens.get(0), 1);

        container.petriNet.markEnabledTransitions();
        container.petriNet.fireTransition(container.transitions.get(0));

        assertTrue("Next transition was enabled", transition.isEnabled());
    }

    @Test
    public void firingTransitionBackwardMovesTokensBack() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        Place place1 = container.places.get(0);
        Place place2 = container.places.get(1);
        Token token = container.tokens.get(0);
        place1.setTokenCount(token, 0);
        place2.setTokenCount(token, 1);

        container.petriNet.fireTransitionBackwards(container.transitions.get(0));

        assertEquals(0, place2.getTokenCount(token));
        assertEquals(1, place1.getTokenCount(token));
    }

    @Test
    public void firingTransitionBackwardEnablesTransition() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token token = container.tokens.get(0);
        container.places.get(0).setTokenCount(token, 0);
        container.places.get(1).setTokenCount(token, 1);

        Transition transition = container.transitions.get(0);
        container.petriNet.fireTransitionBackwards(transition);
        assertTrue("Transition was not enabled", transition.isEnabled());
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

        PetriNet petriNet = new PetriNet();
        Arc arc = new Arc<Place, Transition>(place, transition, arcWeight, new BackwardsNormalStrategy(petriNet));
        Place place2 = new Place("p2", "p2");
        Arc arc2 = new Arc<Transition, Place>(transition, place2, arcWeight, new ForwardsNormalStrategy(petriNet));

        petriNet.addToken(token);
        petriNet.addPlace(place);
        petriNet.addPlace(place2);
        petriNet.addTransition(transition);
        petriNet.addArc(arc);
        petriNet.addArc(arc2);

        place.incrementTokenCount(token);

        PetriNetContainer container = new PetriNetContainer(petriNet);
        container.addArcs(arc, arc2);
        container.addPlaces(place, place2);
        container.addTransitions(transition);
        container.addTokens(token);
        return container;
    }

    /**
     * Create simple petrinet with P1 -o T1 -> P2
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNetContainer createSimpleInhibitorPetriNet(int tokenWeight) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("p1", "p1");
        Transition transition = new Transition("t1", "t1");



        Arc arc = new Arc<Place, Transition>(place, transition, new HashMap<Token, String>(), new InhibitorStrategy());
        Place place2 = new Place("p2", "p2");
        PetriNet petriNet = new PetriNet();
        Map<Token, String> arcWeight = new HashMap<Token, String>();
        arcWeight.put(token, Integer.toString(tokenWeight));
        Arc arc2 = new Arc<Transition, Place>(transition, place2, arcWeight, new ForwardsNormalStrategy(petriNet));

        petriNet.addToken(token);
        petriNet.addPlace(place);
        petriNet.addPlace(place2);
        petriNet.addTransition(transition);
        petriNet.addArc(arc);
        petriNet.addArc(arc2);

        PetriNetContainer container = new PetriNetContainer(petriNet);
        container.addArcs(arc, arc2);
        container.addPlaces(place, place2);
        container.addTransitions(transition);
        container.addTokens(token);
        return container;
    }


    private PetriNetContainer createSelfLoopPetriNet(final int tokenWeight) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("p1", "p1");
        Transition transition = new Transition("t1", "t1");
        Map<Token, String> arcWeight = new HashMap<Token, String>();
        arcWeight.put(token, Integer.toString(tokenWeight));


        PetriNet petriNet = new PetriNet();
        Arc arc = new Arc<Place, Transition>(place, transition, arcWeight, new BackwardsNormalStrategy(petriNet));
        Arc arc2 = new Arc<Transition, Place>(transition, place, arcWeight, new ForwardsNormalStrategy(petriNet));

        petriNet.addToken(token);
        petriNet.addPlace(place);
        petriNet.addTransition(transition);
        petriNet.addArc(arc);
        petriNet.addArc(arc2);

        PetriNetContainer container = new PetriNetContainer(petriNet);
        container.addArcs(arc, arc2);
        container.addPlaces(place);
        container.addTransitions(transition);
        container.addTokens(token);
        return container;

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

        PetriNet petriNet = new PetriNet();
        Arc arc = new Arc<Place, Transition>(place, transition, arcWeight, new BackwardsNormalStrategy(petriNet));
        Place place2 = new Place("p2", "p2");
        Arc arc2 = new Arc<Place, Transition>(place2, transition, arcWeight, new BackwardsNormalStrategy(petriNet));

        petriNet.addToken(token);
        petriNet.addPlace(place);
        petriNet.addPlace(place2);
        petriNet.addTransition(transition);
        petriNet.addArc(arc);
        petriNet.addArc(arc2);

        place.incrementTokenCount(token);

        PetriNetContainer container = new PetriNetContainer(petriNet);
        container.addArcs(arc, arc2);
        container.addPlaces(place, place2);
        container.addTransitions(transition);
        container.addTokens(token);
        return container;
    }

    private class PetriNetContainer {
        public final List<Token> tokens = new ArrayList<Token>();
        public final List<Place> places = new ArrayList<Place>();
        public final List<Transition> transitions = new ArrayList<Transition>();
        public final List<Arc> arcs = new ArrayList<Arc>();
        public final PetriNet petriNet;

        private PetriNetContainer(final PetriNet petriNet) {
            this.petriNet = petriNet;
        }

        public void addTokens(Token... tokens) {
            for (Token token : tokens) {
                this.tokens.add(token);
            }
        }

        public void addArcs(Arc... arcs) {
            for (Arc arc : arcs) {
                this.arcs.add(arc);
            }
        }

        public void addPlaces(Place... places) {
            for (Place place : places) {
                this.places.add(place);
            }
        }

        public void addTransitions(Transition... transitions) {
            for (Transition transition : transitions) {
                this.transitions.add(transition);
            }
        }


    }
}
