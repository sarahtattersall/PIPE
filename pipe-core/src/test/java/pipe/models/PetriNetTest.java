package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.IncidenceMatrix;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetriNetTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    PetriNet net;

    PropertyChangeListener mockListener;

    @Before
    public void setUp() {
        net = new PetriNet();
        mockListener = mock(PropertyChangeListener.class);
    }

    @Test
    public void addingPlaceNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Place place = new Place("", "");
        net.addPlace(place);

        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicatePlaceDoesNotNotifyObservers() {
        Place place = new Place("P0", "P0");
        net.addPlace(place);

        net.addPropertyChangeListener(mockListener);
        net.addPlace(place);
        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));

    }

    @Test
    public void removingPlaceNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Place place = new Place("", "");
        net.addPlace(place);
        net.removePlace(place);

        verify(mockListener, times(2)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingArcNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Arc<? extends Connectable, ? extends Connectable> mockArc = mock(Arc.class);
        net.addArc(mockArc);

        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicateArcDoesNotNotifyObservers() {
        Arc<? extends Connectable, ? extends Connectable> mockArc = mock(Arc.class);
        net.addArc(mockArc);
        net.addPropertyChangeListener(mockListener);
        net.addArc(mockArc);

        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void removingArcNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Arc<? extends Connectable, ? extends Connectable> mockArc = mock(Arc.class);
        Connectable connectable = mock(Connectable.class);
        when(mockArc.getTarget()).thenReturn(connectable);
        when(mockArc.getSource()).thenReturn(connectable);
        net.addArc(mockArc);
        net.removeArc(mockArc);

        verify(mockListener, times(2)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingTransitionNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicateTransitionDoesNotNotifyObservers() {
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        net.addPropertyChangeListener(mockListener);
        net.addTransition(transition);
        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));

    }


    @Test
    public void removingTransitionNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        net.removeTransition(transition);
        verify(mockListener, times(2)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingAnnotationNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Annotation annotation = new Annotation(10, 10, "", 10, 10, false);
        net.addAnnotaiton(annotation);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicateAnnotationDoesNotNotifyObservers() {
        Annotation annotation = new Annotation(10, 10, "", 10, 10, false);
        net.addAnnotaiton(annotation);
        net.addPropertyChangeListener(mockListener);
        net.addAnnotaiton(annotation);
        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingRateParameterNotifiesObservers() {
        //        net.addPropertyChangeListener(mockListener);
        //        RateParameter rateParameter = new RateParameter("", 0., 0, 0);
        //        net.addRate(rateParameter);
        //        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingTokenNotifiesObservers() {

        net.addPropertyChangeListener(mockListener);
        Token token = new Token();
        net.addToken(token);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicateTokenDoesNotNotifyObservers() {
        Token token = new Token();
        net.addToken(token);

        net.addPropertyChangeListener(mockListener);
        net.addToken(token);
        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingStateGroupNotifiesObservers() {

        //        net.addPropertyChangeListener(mockListener);
        //        StateGroup group = new StateGroup();
        //        net.addStateGroup(group);
        //        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
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
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, ArcType.NORMAL);
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

    //    @Test
    //    public void registersItselfAsPlaceObserver() {
    //        Place place = mock(Place.class);
    //        net.addPlace(place);
    //        verify(place).addPropertyChangeListener(net);
    //    }
    //
    //
    //    @Test
    //    public void registersItselfAsTransitionObserver() {
    //        Transition transition = mock(Transition.class);
    //        net.addTransition(transition);
    //        verify(transition).addPropertyChangeListener(net);
    //    }
    //
    //
    //    @Test
    //    public void registersItselfAsArcObserver() {
    //        Arc arc = mock(Arc.class);
    //        net.addArc(arc);
    //        verify(arc).registerObserver(net);
    //    }

    @Test
    public void correctForwardIncidenceMatrix() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        IncidenceMatrix forwardMatrix = container.petriNet.getForwardsIncidenceMatrix(container.tokens.get(0));

        Transition transition = container.transitions.get(0);
        assertEquals(tokenWeight, forwardMatrix.get(container.places.get(1), transition));
        assertEquals(0, forwardMatrix.get(container.places.get(0), transition));
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

        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, arcWeight, ArcType.NORMAL);
        Place place2 = new Place("p2", "p2");
        Arc<Transition, Place> arc2 = new Arc<Transition, Place>(transition, place2, arcWeight, ArcType.NORMAL);

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

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
        assertTrue("Petri net did not put transition in enabled collection",
                enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesEnabledWithNoSecondColourToken() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arcs.get(0).setWeight(redToken, "0");

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
        assertTrue("Petri net did not put transition in enabled collection",
                enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToEmptyPlace() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);
        container.places.get(0).decrementTokenCount(container.tokens.get(0));

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToNotEnoughTokens() {
        int tokenWeight = 4;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToOnePlaceNotEnoughTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNetTwoPlacesToTransition(tokenWeight);

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
        assertFalse("Petri net put transition in enabled collection", enabled.contains(container.transitions.get(0)));
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
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, arcWeight, ArcType.NORMAL);
        Place place2 = new Place("p2", "p2");
        Arc<Place, Transition> arc2 = new Arc<Place, Transition>(place2, transition, arcWeight, ArcType.NORMAL);

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

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToArcNeedingTwoDifferentTokens() {
        int tokenWeight = 1;
        PetriNetContainer container = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", true, 0, new Color(255, 0, 0));
        container.petriNet.addToken(redToken);
        container.arcs.get(0).getTokenWeights().put(redToken, "1");

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
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

        Collection<Transition> enabled = container.petriNet.getEnabledTransitions();
        assertTrue("Petri net did not put transition in enabled collection",
                enabled.contains(container.transitions.get(0)));
    }

    @Test
    public void correctlyMarksEnabledTransitions() {
        PetriNetContainer container = createSimplePetriNet(1);
        container.petriNet.markEnabledTransitions();
        assertTrue("Did not enable transition", container.transitions.get(0).isEnabled());
    }

    @Test
    public void onlyEnablesHigherPriorityTransition() {
        PetriNet net = new PetriNet();
        Transition t1 = new Transition("1", "1");
        t1.setPriority(10);
        Transition t2 = new Transition("1", "1");
        t2.setPriority(1);
        net.addTransition(t1);
        net.addTransition(t2);
        net.markEnabledTransitions();

        assertTrue(t1.isEnabled());
        assertFalse(t2.isEnabled());
    }

    @Test
    public void correctlyDoesNotMarkNotEnabledTransitions() {
        PetriNetContainer container = createSimplePetriNet(2);
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

    private PetriNetContainer createSelfLoopPetriNet(final int tokenWeight) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("p1", "p1");
        Transition transition = new Transition("t1", "t1");
        Map<Token, String> arcWeight = new HashMap<Token, String>();
        arcWeight.put(token, Integer.toString(tokenWeight));


        PetriNet petriNet = new PetriNet();
        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, arcWeight, ArcType.NORMAL);
        Arc<Transition, Place> arc2 = new Arc<Transition, Place>(transition, place, arcWeight, ArcType.NORMAL);

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

    @Test
    public void correctlyMarksInhibitorArcEnabledTransition() {
        PetriNetContainer container = createSimpleInhibitorPetriNet(1);
        container.petriNet.markEnabledTransitions();
        assertTrue("Did not enable transition when it can fire", container.transitions.get(0).isEnabled());
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


        Arc arc = new Arc<Place, Transition>(place, transition, new HashMap<Token, String>(), ArcType.INHIBITOR);
        Place place2 = new Place("p2", "p2");
        PetriNet petriNet = new PetriNet();
        Map<Token, String> arcWeight = new HashMap<Token, String>();
        arcWeight.put(token, Integer.toString(tokenWeight));

        Arc<Transition, Place> arc2 = new Arc<Transition, Place>(transition, place2, arcWeight, ArcType.NORMAL);

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

        Arc<Place, Transition> arc3 =
                new Arc<Place, Transition>(container.places.get(1), transition, container.arcs.get(0).getTokenWeights(),
                        ArcType.NORMAL);
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

    private class PetriNetContainer {
        public final List<Token> tokens = new ArrayList<Token>();

        public final List<Place> places = new ArrayList<Place>();

        public final List<Transition> transitions = new ArrayList<Transition>();

        public final List<Arc<? extends Connectable, ? extends Connectable>> arcs =
                new ArrayList<Arc<? extends Connectable, ? extends Connectable>>();

        public final PetriNet petriNet;

        private PetriNetContainer(PetriNet petriNet) {
            this.petriNet = petriNet;
        }

        public void addTokens(Token... tokens) {
            Collections.addAll(this.tokens, tokens);
        }

        public void addArcs(Arc<? extends Connectable, ? extends Connectable>... arcs) {
            Collections.addAll(this.arcs, arcs);
        }

        public void addPlaces(Place... places) {
            Collections.addAll(this.places, places);
        }

        public void addTransitions(Transition... transitions) {
            Collections.addAll(this.transitions, transitions);
        }


    }
}
