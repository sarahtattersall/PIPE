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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ANormalArcTest {
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
                ANormalArc.withSource("P0").andTarget("T1").create(tokens, connectables, rateParameters);

        Arc<? extends Connectable, ? extends Connectable> expected =
                new Arc<>(connectables.get("P0"), connectables.get("T1"), new HashMap<Token, String>(), ArcType.NORMAL);

        assertEquals(expected, arc);
    }

    @Test
    public void createsArcSingleToken() {
        tokens.put("Red", new Token("Red", Color.RED));
        connectables.put("P0", new Place("P0", "P0"));
        connectables.put("T1", new Transition("T1", "T1"));
        Arc<? extends Connectable, ? extends Connectable> arc =
                ANormalArc.withSource("P0").andTarget("T1").with("4", "Red").tokens().create(tokens, connectables,
                        rateParameters);

        HashMap<Token, String> tokenWeights = new HashMap<>();
        tokenWeights.put(tokens.get("Red"), "4");
        Arc<? extends Connectable, ? extends Connectable> expected =
                new Arc<>(connectables.get("P0"), connectables.get("T1"), tokenWeights, ArcType.NORMAL);

        assertEquals(expected, arc);
    }

    @Test
    public void createsArcWithMultipleTokens() {
        tokens.put("Red", new Token("Red", Color.RED));
        tokens.put("Default", new Token("Default", Color.BLACK));
        connectables.put("P0", new Place("P0", "P0"));
        connectables.put("T1", new Transition("T1", "T1"));
        Arc<? extends Connectable, ? extends Connectable> arc =
                ANormalArc.withSource("P0").andTarget("T1").with("4", "Red").tokens().and("1", "Default").token().create(tokens, connectables,
                        rateParameters);

        HashMap<Token, String> tokenWeights = new HashMap<>();
        tokenWeights.put(tokens.get("Red"), "4");
        tokenWeights.put(tokens.get("Default"), "1");
        Arc<? extends Connectable, ? extends Connectable> expected =
                new Arc<>(connectables.get("P0"), connectables.get("T1"), tokenWeights, ArcType.NORMAL);

        assertEquals(expected, arc);
    }

}

