package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ATokenTest {
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
    public void createsTokenWithNameAndDefaultColorBlack() {
        Token token = AToken.called("Default").create(tokens, places, transitions, rateParameters);
        Token expected = new Token("Default", Color.BLACK);
        assertEquals(expected, token);
    }

    @Test
    public void createsTokenWithSpecifiedColor() {
        Token token = AToken.called("Red").withColor(Color.RED).create(tokens, places, transitions, rateParameters);
        Token expected = new Token("Red", Color.RED);
        assertEquals(expected, token);
    }

    @Test
    public void addsToTokens() {
        Token token = AToken.called("Default").create(tokens, places, transitions, rateParameters);
        assertThat(tokens).containsEntry("Default", token);
    }
}
