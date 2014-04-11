package pipe.dsl;

import org.junit.Test;
import pipe.exceptions.InvalidRateException;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class APetriNetTest {
    @Test
    public void createsPetriNetWithOnePlace() {
        PetriNet petriNet = APetriNet.withOnly(APlace.withId("P0"));

        PetriNet expected = new PetriNet();
        Place place = new Place("P0", "P0");
        expected.addPlace(place);

        assertEquals(expected, petriNet);
    }

    @Test
    public void createsPetriNetWithMultipleItems() {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.RED))
                                     .and(APlace.withId("P0"))
                                     .and(ATransition.withId("T0"))
                                     .andFinally(ANormalArc.withSource("P0").andTarget("T0").with("5", "Default").tokens());

        PetriNet expected = new PetriNet();
        Token token = new Token("Default", Color.RED);
        expected.addToken(token);
        Place place = new Place("P0", "P0");
        expected.addPlace(place);
        Transition transition = new Transition("T0", "T0");
        expected.addTransition(transition);
        Map<Token, String> arcWeights = new HashMap<>();
        arcWeights.put(token, "5");
        Arc<Place, Transition> arc = new Arc<>(place, transition, arcWeights, ArcType.NORMAL);
        expected.addArc(arc);

        assertEquals(expected, petriNet);
    }

    /**
     * This is an example of creating everything.
     * It shows how much space DSL saves
     */
    @Test
    public void createColoredPetriNet() throws InvalidRateException {
        PetriNet petriNet = APetriNet.with(AToken.called("Red").withColor(Color.RED))
                .and(AToken.called("Blue").withColor(Color.BLUE))
                .and(ARateParameter.withId("Foo").andExpression("10"))
                .and(APlace.withId("P0").andCapacity(10).containing(5, "Blue").tokens().and(2, "Red").tokens())
                .and(APlace.withId("P1"))
                .and(ATransition.withId("T0").whichIsTimed().withRateParameter("Foo"))
                .and(AnInhibitorArc.withSource("P1").andTarget("T0"))
                .andFinally(
                        ANormalArc.withSource("P0").andTarget("T0").with("5", "Red").tokens().and("1", "Blue").token());

        PetriNet expected = new PetriNet();
        Token red = new Token("Red",Color.RED);
        expected.addToken(red);

        Token blue = new Token("Blue", Color.BLUE);
        expected.addToken(blue);

        RateParameter rateParameter = new RateParameter("10", "Foo", "Foo");
        expected.addRateParameter(rateParameter);

        Place p0 = new Place("P0", "P0");
        p0.setCapacity(10);
        Map<Token, Integer> p0Tokens = new HashMap<>();
        p0Tokens.put(blue, 5);
        p0Tokens.put(red, 2);
        p0.setTokenCounts(p0Tokens);
        expected.addPlace(p0);

        Place p1 = new Place("P1", "P1");
        expected.addPlace(p1);

        Transition t0 = new Transition("T0", "T0");
        t0.setTimed(true);
        t0.setRate(rateParameter);
        expected.addTransition(t0);

        Map<Token, String> arcWeights = new HashMap<>();
        arcWeights.put(red, "5");
        arcWeights.put(blue, "1");
        Arc<Place, Transition> normalArc = new Arc<>(p0, t0, arcWeights, ArcType.NORMAL);
        expected.addArc(normalArc);

        //Apologies, this shouldn't really be passing in a weight. It's on my todo list to refactor
        Arc<Place, Transition> inihibArc = new Arc<>(p1, t0, new HashMap<Token, String>(), ArcType.INHIBITOR);
        expected.addArc(inihibArc);

        assertEquals(expected, petriNet);
    }

}
