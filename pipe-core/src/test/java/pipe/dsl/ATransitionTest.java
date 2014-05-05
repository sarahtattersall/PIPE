package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.place.Place;
import pipe.models.component.rate.NormalRate;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ATransitionTest {

    private Map<String, Token> tokens;

    private Map<String, Place> places;

    private Map<String, Transition> transitions;

    private Map<String, RateParameter> rateParameters;


    @Before
    public void setUp() {
        tokens = new HashMap<>();
        places = new HashMap<>();
        transitions = new HashMap<>();
        rateParameters = new HashMap<>();
    }

    @Test
    public void createsTransitionWithId() {
        Transition transition = ATransition.withId("T0").create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        assertEquals(expected, transition);
    }

    @Test
    public void addsTransitionToConnectables() {
        Transition transition = ATransition.withId("T0").create(tokens, places, transitions, rateParameters);
        assertThat(transitions).containsEntry("T0", transition);
    }

    @Test
    public void createsTransitionWithPriority() {
        Transition transition =
                ATransition.withId("T0").andPriority(5).create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setPriority(5);
        assertEquals(expected, transition);
    }


    @Test
    public void createsTimedTransition() {
        Transition transition =
                ATransition.withId("T0").whichIsTimed().create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setTimed(true);
        assertEquals(expected, transition);
    }


    @Test
    public void createsImmediateTransition() {
        Transition transition =
                ATransition.withId("T0").whichIsImmediate().create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setTimed(false);
        assertEquals(expected, transition);
    }

    @Test
    public void createsInfiniteServerTransition() {
        Transition transition =
                ATransition.withId("T0").andIsAnInfinite().server().create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setInfiniteServer(true);
        assertEquals(expected, transition);
    }

    @Test
    public void createsSingleServerTransition() {
        Transition transition =
                ATransition.withId("T0").andIsASingle().server().create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setInfiniteServer(false);
        assertEquals(expected, transition);
    }

    @Test
    public void createsNormalRateTransition() {
        Transition transition =
                ATransition.withId("T0").andRate("5").create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setRate(new NormalRate("5"));
        assertEquals(expected, transition);
    }

    @Test
    public void createsTransitionWithARateParameter() {
        rateParameters.put("Foo", new RateParameter("5", "Foo", "Foo"));
        Transition transition =
                ATransition.withId("T0").withRateParameter("Foo").create(tokens, places, transitions, rateParameters);
        Transition expected = new Transition("T0", "T0");
        expected.setRate(rateParameters.get("Foo"));
        assertEquals(expected, transition);
    }


}
