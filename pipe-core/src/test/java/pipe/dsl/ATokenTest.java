package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Connectable;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ATokenTest {
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
    public void createsTokenWithNameAndDefaultColorBlack() {
        Token token = AToken.called("Default").create(tokens, connectables, rateParameters);
        Token expected = new Token("Default", true, 0, Color.BLACK);
        assertEquals(expected, token);
    }

    @Test
    public void createsTokenWithSpecifiedColor() {
        Token token = AToken.called("Red").withColor(Color.RED).create(tokens, connectables, rateParameters);
        Token expected = new Token("Red", true, 0, Color.RED);
        assertEquals(expected, token);
    }

    @Test
    public void addsToTokens() {
        Token token = AToken.called("Default").create(tokens, connectables, rateParameters);
        assertThat(tokens).containsEntry("Default", token);
    }
}
