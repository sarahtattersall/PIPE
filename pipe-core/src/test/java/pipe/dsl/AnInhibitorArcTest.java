package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AnInhibitorArcTest {
    private Map<String, Token> tokens;

    private Map<String, Connectable> connectables;

    private Map<String, RateParameter> rateParameters;

    @Before
    public void setUp() {
        tokens = new HashMap<>();
        connectables = new HashMap<>();
        rateParameters = new HashMap<>();
    }

    @Test
    public void createsArcWithSourceAndTarget() {
        connectables.put("P0", new Place("P0", "P0"));
        connectables.put("T1", new Transition("T1", "T1"));
        Arc<? extends Connectable, ? extends Connectable> arc =
                AnInhibitorArc.withSource("P0").andTarget("T1").create(tokens, connectables, rateParameters);

        Arc<? extends Connectable, ? extends Connectable> expected =
                new Arc<>(connectables.get("P0"), connectables.get("T1"), new HashMap<Token, String>(), ArcType.INHIBITOR);

        assertEquals(expected, arc);
    }
}
