package pipe.petrinet.unfold;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
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

    private Token getRedToken() {
        return new Token("Red", true, 0, new Color(255, 0, 0));
    }

    private Token getBlackToken() {
        return new Token("Black", true, 0, new Color(0, 0, 0));
    }

    private Token getDefaultToken() {
        return new Token("Default", true, 0, new Color(0, 0, 0));
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
        Token token = getDefaultToken();
        petriNet.addToken(token);

        Place place = new Place("P0", "P0");
        petriNet.addPlace(place);
        Transition transition = new Transition("T0", "T0");
        petriNet.addTransition(transition);

        Map<Token, String> weights = new HashMap<Token, String>();
        weights.put(token, "2");

        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, ArcType.NORMAL);
        petriNet.addArc(arc);

        expander = new Expander(petriNet);

        PetriNet expected = new PetriNet();
        Place newPlace = new Place("P0_Default", "P0_Default");
        expected.addPlace(newPlace);
        expected.add(transition);
        expected.add(new Arc<Place, Transition>(newPlace, transition, weights, ArcType.NORMAL));

        PetriNet unfolded = expander.unfold();
        checkPetriNetsEqual(expected, unfolded);
    }

    private void checkPetriNetsEqual(PetriNet expected, PetriNet actual) {
        assertThat(actual.getPlaces(), IsIterableContainingInOrder.contains(expected.getPlaces().toArray()));
        assertThat(actual.getTransitions(), IsIterableContainingInOrder.contains(expected.getTransitions().toArray()));
        assertThat(actual.getArcs(), IsIterableContainingInOrder.contains(expected.getArcs().toArray()));
    }

    @Test
    public void simpleNetPetriNetIsExpandedToAddExtraPlaceTransition() {
        Token token = getDefaultToken();
        Token redToken = getRedToken();
        petriNet.addToken(token);
        petriNet.addToken(redToken);

        Place place = new Place("P0", "P0");
        place.setTokenCount(redToken, 1);
        petriNet.addPlace(place);
        Transition transition = new Transition("T0", "T0");
        petriNet.addTransition(transition);

        Map<Token, String> weights = new HashMap<Token, String>();
        weights.put(token, "1");
        weights.put(redToken, "2");

        Arc<Place, Transition> arc = new Arc<Place, Transition>(place, transition, weights, ArcType.NORMAL);
        petriNet.addArc(arc);

        expander = new Expander(petriNet);

        PetriNet expected = new PetriNet();
        //TODO: THIS ISNT THE BEST BECAUSE DUE TO HASHMAP IT MIGHT BE IN A IDFF ORDER
        // WORK OUT A BETTER WAY TO CHECK
        Place newPlace = new Place("P0_Red_Default", "P0_Red_Default");
        expected.addPlace(newPlace);
        expected.add(transition);
        expected.add(new Arc<Place, Transition>(newPlace, transition, weights, ArcType.NORMAL));

        PetriNet unfolded = expander.unfold();
        checkPetriNetsEqual(expected, unfolded);
    }
}
