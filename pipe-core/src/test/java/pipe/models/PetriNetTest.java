package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.dsl.*;
import pipe.exceptions.InvalidRateException;
import pipe.exceptions.PetriNetComponentException;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.arc.InboundNormalArc;
import pipe.models.component.place.Place;
import pipe.models.component.rate.NormalRate;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.IncidenceMatrix;
import pipe.models.petrinet.PetriNet;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PetriNetTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PetriNet net;

    @Mock
    private PropertyChangeListener mockListener;

    @Before
    public void setUp() {
        net = new PetriNet();
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
        InboundArc mockArc = mock(InboundArc.class);
        net.addArc(mockArc);

        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicateArcDoesNotNotifyObservers() {
        InboundArc mockArc = mock(InboundArc.class);
        net.addArc(mockArc);
        net.addPropertyChangeListener(mockListener);
        net.addArc(mockArc);

        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void removingArcNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        InboundArc mockArc = mock(InboundArc.class);
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        when(mockArc.getTarget()).thenReturn(transition);
        when(mockArc.getSource()).thenReturn(place);
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
    public void cannotRemoveTokenIfPlaceDependsOnIt() throws PetriNetComponentException {
        expectedException.expect(PetriNetComponentException.class);
        expectedException.expectMessage("Cannot remove Default token places: P0 contain it");
        Token token = new Token("Default", Color.BLACK);
        Place place = new Place("P0", "P0");
        place.setTokenCount(token.getId(), 2);
        net.addPlace(place);

        net.removeToken(token);
    }

    @Test
    public void cannotRemoveTokenIfTransitionReferencesIt() throws PetriNetComponentException {
        expectedException.expect(PetriNetComponentException.class);
        expectedException.expectMessage("Cannot remove Default token transitions: T0 reference it\n");
        Token token = new Token("Default", Color.BLACK);
        Transition transition = new Transition("T0", "T0");
        transition.setRate(new NormalRate("#(P0, Default)"));
        net.addTransition(transition);
        net.removeToken(token);
    }


    @Test
    public void addingAnnotationNotifiesObservers() {
        net.addPropertyChangeListener(mockListener);
        Annotation annotation = new Annotation(10, 10, "", 10, 10, false);
        net.addAnnotation(annotation);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingDuplicateAnnotationDoesNotNotifyObservers() {
        Annotation annotation = new Annotation(10, 10, "", 10, 10, false);
        net.addAnnotation(annotation);
        net.addPropertyChangeListener(mockListener);
        net.addAnnotation(annotation);
        verify(mockListener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void addingRateParameterNotifiesObservers() throws InvalidRateException {
        net.addPropertyChangeListener(mockListener);
        RateParameter rateParameter = new RateParameter("5", "id", "name");
        net.addRateParameter(rateParameter);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
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
    public void genericRemoveMethodRemovesPlace() throws PetriNetComponentException {
        Place place = new Place("", "");
        net.addPlace(place);

        assertEquals(1, net.getPlaces().size());
        net.remove(place);
        assertTrue(net.getPlaces().isEmpty());
    }

    @Test
    public void genericRemoveMethodRemovesArc() throws PetriNetComponentException {
        Place place = new Place("source", "source");
        Transition transition = new Transition("target", "target");
        Map<String, String> weights = new HashMap<>();
        InboundNormalArc arc = new InboundNormalArc(place, transition, weights);
        net.addArc(arc);

        assertEquals(1, net.getArcs().size());
        net.remove(arc);
        assertTrue(net.getArcs().isEmpty());
    }

    @Test
    public void returnsCorrectToken() throws PetriNetComponentNotFoundException {
        String id = "Token1";
        Color color = new Color(132, 16, 130);
        Token token = new Token(id, color);
        net.addToken(token);
        assertEquals(token, net.getComponent(id, Token.class));
    }

    @Test
    public void throwsErrorIfNoTokenExists() throws PetriNetComponentNotFoundException {
        expectedException.expect(PetriNetComponentNotFoundException.class);
        expectedException.expectMessage("No component foo exists in Petri net");
        net.getComponent("foo", Token.class);
    }

    @Test
    public void correctlyGeneratesForwardIncidenceMatrix() {
        PetriNet petriNet =
                APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P1")).and(
                        APlace.withId("P2")).and(ATransition.withId("T1")).and(
                        ANormalArc.withSource("P1").andTarget("T1").with("4", "Default").tokens()).andFinally(
                        ANormalArc.withSource("T1").andTarget("P2").with("4", "Default").tokens());

        Token token = getComponent("Default", petriNet.getTokens());

        IncidenceMatrix forwardMatrix = petriNet.getForwardsIncidenceMatrix(token.getId());

        Transition transition = getComponent("T1", petriNet.getTransitions());
        Place p1 = getComponent("P1", petriNet.getPlaces());
        Place p2 = getComponent("P2", petriNet.getPlaces());

        assertEquals(0, forwardMatrix.get(p1, transition));
        assertEquals(4, forwardMatrix.get(p2, transition));
    }

    private <T extends PetriNetComponent> T getComponent(String id, Iterable<T> components) {
        for (T component : components) {
            if (component.getId().equals(id)) {
                return component;
            }
        }
        return null;
    }

    @Test
    public void throwsExceptionIfNoRateParameterExists() throws PetriNetComponentNotFoundException {
        expectedException.expect(PetriNetComponentNotFoundException.class);
        expectedException.expectMessage("No rate parameter foo exists in Petri net");
        net.getRateParameter("foo");
    }

    @Test
    public void throwsExceptionIfRateParameterIsNotValid() throws InvalidRateException {
        expectedException.expect(InvalidRateException.class);
        expectedException.expectMessage("Rate of hsfg is invalid");
        RateParameter rateParameter = new RateParameter("hsfg", "id", "name");
        net.addRateParameter(rateParameter);
    }

    @Test
    public void correctBackwardIncidenceMatrix() {
        int tokenWeight = 4;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Token token = getComponent("Default", petriNet.getTokens());
        Transition transition = getComponent("T1", petriNet.getTransitions());

        Place p1 = getComponent("P1", petriNet.getPlaces());
        Place p2 = getComponent("P2", petriNet.getPlaces());

        IncidenceMatrix backwardIncidence = petriNet.getBackwardsIncidenceMatrix(token.getId());
        assertEquals(tokenWeight, backwardIncidence.get(p1, transition));
        assertEquals(0, backwardIncidence.get(p2, transition));
    }

    /**
     * Create simple petrinet with P1 -> T1 -> P2
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNet createSimplePetriNet(int tokenWeight) {
        String arcWeight = Integer.toString(tokenWeight);
        return APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P1").containing(1, "Default").token()).and(APlace.withId("P2")).and(
                ATransition.withId("T1")).and(
                ANormalArc.withSource("P1").andTarget("T1").with(arcWeight, "Default").tokens()).andFinally(
                ANormalArc.withSource("T1").andTarget("P2").with(arcWeight, "Default").tokens());
    }

    /**
     * Create simple petrinet with P1 -> T1 and P2 -> T1
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNet createSimplePetriNetTwoPlacesToTransition(int tokenWeight) {
        String weight = Integer.toString(tokenWeight);
        return APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P1")).and(
                APlace.withId("P2")).and(ATransition.withId("T1")).and(
                ANormalArc.withSource("P1").andTarget("T1").with(weight, "Default").tokens()).andFinally(
                ANormalArc.withSource("P2").andTarget("T1").with(weight, "Default").tokens());
    }

    @Test
    public void deletingRateParameterRemovesItFromTransition() throws InvalidRateException {
        String rate = "5.0";
        RateParameter rateParameter = new RateParameter(rate, "R0", "R0");
        Transition transition = new Transition("T0", "T0", rateParameter, 0);

        net.addRateParameter(rateParameter);
        net.addTransition(transition);

        net.removeRateParameter(rateParameter);

        assertEquals(rate, transition.getRateExpr());
        assertTrue("Transition rate was not changed to normal rate on deletion",
                transition.getRate() instanceof NormalRate);
    }

    @Test
    public void testEqualityEqualPetriNets() {
        PetriNet net1 = createSimplePetriNet(1);
        PetriNet net2 = createSimplePetriNet(1);
        assertTrue(net1.equals(net2));
    }

    @Test
    public void testEqualityNotEqualPetriNets() {
        PetriNet net1 = createSimplePetriNet(1);
        PetriNet net2 = createSimplePetriNet(4);
        assertFalse(net1.equals(net2));
    }

    @Test
    public void equalsAndHashCodeLawsWhenEqual() {
        PetriNet net1 = createSimplePetriNet(1);
        PetriNet net2 = createSimplePetriNet(1);
        assertTrue(net1.equals(net2));
        assertEquals(net1.hashCode(), net2.hashCode());
    }

    @Test
    public void equalsAndHashCodeLawsWhenNotEqual() {
        PetriNet net1 = createSimplePetriNet(1);
        PetriNet net2 = createSimplePetriNet(5);
        assertFalse(net1.equals(net2));
    }

    @Test
    public void canGetTokenById() throws PetriNetComponentNotFoundException {
        Token t = new Token("Default", Color.BLACK);
        net.addToken(t);
        assertEquals(t, net.getComponent(t.getId(), Token.class));
    }

    @Test
    public void canGetTokenByIdAfterNameChange() throws PetriNetComponentNotFoundException {
        Token t = new Token("Default", Color.BLACK);
        net.addToken(t);
        t.setId("Red");
        assertEquals(t, net.getComponent(t.getId(), Token.class));
    }

    @Test
    public void canGetPlaceById() throws PetriNetComponentNotFoundException {
        Place p = new Place("P0", "P0");
        net.addPlace(p);
        assertEquals(p, net.getComponent(p.getId(), Place.class));
    }

    @Test
    public void canGetPlaceByIdAfterIdChange() throws PetriNetComponentNotFoundException {
        Place p = new Place("P0", "P0");
        net.addPlace(p);
        p.setId("P1");
        assertEquals(p, net.getComponent(p.getId(), Place.class));
    }

    @Test
    public void canGetRateParameterById() throws PetriNetComponentNotFoundException, InvalidRateException {
        RateParameter r = new RateParameter("2", "R0", "R0");
        net.addRateParameter(r);
        assertEquals(r, net.getComponent(r.getId(), RateParameter.class));
    }

    @Test
    public void canGetRateParameterByIdAfterIdChange() throws PetriNetComponentNotFoundException, InvalidRateException {
        RateParameter r = new RateParameter("2", "R0", "R0");
        net.addRateParameter(r);
        r.setId("R1");
        assertEquals(r, net.getComponent(r.getId(), RateParameter.class));
    }

    @Test
    public void canGetTransitionById() throws PetriNetComponentNotFoundException {
        Transition t = new Transition("T0", "T0");
        net.addTransition(t);
        assertEquals(t, net.getComponent(t.getId(), Transition.class));
    }

    @Test
    public void canGetTransitionByIdAfterNameChange() throws PetriNetComponentNotFoundException {
        Transition t = new Transition("T0", "T0");
        net.addTransition(t);
        t.setId("T2");
        assertEquals(t, net.getComponent(t.getId(), Transition.class));
    }

    @Test
    public void canGetArcById() throws PetriNetComponentNotFoundException {
        Place p = new Place("P0", "P0");
        Transition t = new Transition("T0", "T0");
        InboundArc a = new InboundNormalArc(p, t, new HashMap<String, String>());
        net.addArc(a);
        assertEquals(a, net.getComponent(a.getId(), InboundArc.class));
    }

    @Test
    public void canGetArcByIdAfterNameChange() throws PetriNetComponentNotFoundException {
        Place p = new Place("P0", "P0");
        Transition t = new Transition("T0", "T0");
        InboundArc a = new InboundNormalArc(p, t, new HashMap<String, String>());
        net.addArc(a);
        a.setId("A1");
        assertEquals(a, net.getComponent(a.getId(), InboundArc.class));
    }
}
