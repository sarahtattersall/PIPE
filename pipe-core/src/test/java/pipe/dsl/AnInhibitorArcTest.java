package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.InboundInhibitorArc;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AnInhibitorArcTest {
    private Map<String, Token> tokens;

    private Map<String, Place> places;

    private Map<String, RateParameter> rateParameters;

    private Map<String, Transition> transitions;

    @Before
    public void setUp() {
        tokens = new HashMap<>();
        places = new HashMap<>();
        transitions = new HashMap<>();
        rateParameters = new HashMap<>();
    }

    @Test
    public void createsArcWithSourceAndTarget() {
        places.put("P0", new Place("P0", "P0"));
        transitions.put("T1", new Transition("T1", "T1"));
        Arc<? extends Connectable, ? extends Connectable> arc =
                AnInhibitorArc.withSource("P0").andTarget("T1").create(tokens, places, transitions, rateParameters);

        Arc<? extends Connectable, ? extends Connectable> expected =
                new InboundInhibitorArc(places.get("P0"), transitions.get("T1"));

        assertEquals(expected, arc);
    }
}
