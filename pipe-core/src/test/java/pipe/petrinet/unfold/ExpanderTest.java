package pipe.petrinet.unfold;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import pipe.dsl.*;
import pipe.exceptions.PetriNetComponentException;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.arc.InboundNormalArc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ExpanderTest {
    Expander expander;

    PetriNet petriNet;

    @Before
    public void setUp() {
        petriNet = new PetriNet();
    }

    @Test
    public void usesDefaultTokenAsFirstChoice() {
        Token token = getDefaultToken();
        Token blackToken = getBlackToken();
        Token otherToken = getRedToken();

        petriNet.addToken(token);
        petriNet.addToken(blackToken);
        petriNet.addToken(otherToken);

        expander = new Expander(petriNet);
        PetriNet unfolded = expander.unfold();

        assertTrue("Unfolding produced the wrong number of tokens", unfolded.getTokens().size() == 1);
        Token actualToken = unfolded.getTokens().iterator().next();
        assertEquals("Token was not default token", token, actualToken);

    }

    private Token getDefaultToken() {
        return new Token("Default", new Color(0, 0, 0));
    }

    private Token getBlackToken() {
        return new Token("Black", new Color(0, 0, 0));
    }

    private Token getRedToken() {
        return new Token("Red", new Color(255, 0, 0));
    }

    @Test
    public void usesBlackTokenAsSecondChoice() {
        Token blackToken = getBlackToken();
        Token otherToken = getRedToken();

        petriNet.addToken(blackToken);
        petriNet.addToken(otherToken);

        expander = new Expander(petriNet);
        PetriNet unfolded = expander.unfold();

        assertTrue("Unfolding produced the wrong number of tokens", unfolded.getTokens().size() == 1);
        Token actualToken = unfolded.getTokens().iterator().next();
        assertEquals("Token was not black token", blackToken, actualToken);

    }

    @Test
    public void usesOtherTokenAsLastResort() {
        Token otherToken = getRedToken();

        petriNet.addToken(otherToken);

        expander = new Expander(petriNet);
        PetriNet unfolded = expander.unfold();

        assertTrue("Unfolding produced the wrong number of tokens", unfolded.getTokens().size() == 1);
        Token actualToken = unfolded.getTokens().iterator().next();
        assertEquals("Token was not red token", otherToken, actualToken);
    }

    @Test
    public void singleTokenPetriNetIsExpandedToItself() {
        petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P0")).and(
                ATransition.withId("T0")).andFinally(
                ANormalArc.withSource("P0").andTarget("T0").with("2", "Default").tokens());

        expander = new Expander(petriNet);

        PetriNet expected =
                APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P0_Default")).and(
                        ATransition.withId("T0")).andFinally(
                        ANormalArc.withSource("P0_Default").andTarget("T0").with("2", "Default").tokens());

        PetriNet unfolded = expander.unfold();
        checkPetriNetsEqual(expected, unfolded);
    }

    private void checkPetriNetsEqual(PetriNet expected, PetriNet actual) {
        assertThat(actual.getPlaces(), IsIterableContainingInOrder.contains(expected.getPlaces().toArray()));
        assertThat(actual.getTransitions(), IsIterableContainingInOrder.contains(expected.getTransitions().toArray()));
        assertThat(actual.getArcs(), IsIterableContainingInOrder.contains(expected.getArcs().toArray()));
    }

    @Test
    public void simpleNetPetriNetIsExpandedToAddExtraPlaceTransition() throws PetriNetComponentException {
        Token token = getDefaultToken();
        Token redToken = getRedToken();
        petriNet.addToken(token);
        petriNet.addToken(redToken);

        Place place = new Place("P0", "P0");
        place.setTokenCount(redToken.getId(), 1);
        petriNet.addPlace(place);
        Transition transition = new Transition("T0", "T0");
        petriNet.addTransition(transition);

        Map<String, String> weights = new HashMap<>();
        weights.put(token.getId(), "1");
        weights.put(redToken.getId(), "2");

        InboundArc arc = new InboundNormalArc(place, transition, weights);
        petriNet.addArc(arc);

        expander = new Expander(petriNet);

        PetriNet expected = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P0_Default_Red").and(1, "Default").token()).and(ATransition.withId("T0")).andFinally(
                ANormalArc.withSource("P0_Default_Red").andTarget("T0").with("1", "Default").token().and("2", "Red").tokens());

        PetriNet unfolded = expander.unfold();
        checkPetriNetsEqual(expected, unfolded);
    }
}
