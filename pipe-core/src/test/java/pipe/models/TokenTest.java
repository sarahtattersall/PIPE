package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.models.component.token.Token;

import java.awt.Color;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TokenTest {

    Token token;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        token = new Token();
    }

    /**
     * Test tokens are compared via their tokenName and color
     */
    @Test
    public void tokenEquality() {
        String tokenName = "Default";
        Color sameColor = new Color(0, 0, 0);
        Token token1 = new Token(tokenName, sameColor);
        Token token2 = new Token(tokenName, sameColor);

        assertEquals(token1, token2);
    }

    @Test
    public void tokenNameInequality() {
        String tokenName = "Default";
        Color sameColor = new Color(0, 0, 0);
        Token token1 = new Token(tokenName, sameColor);
        Token token2 = new Token(tokenName + "different", sameColor);

        assertThat(token1, is(not(equalTo((token2)))));
    }


    @Test
    public void tokenColorInequality() {
        String tokenName = "Default";
        Color color1 = new Color(0, 0, 0);
        Token token1 = new Token(tokenName, color1);
        Color color2 = new Color(255, 255, 1);
        Token token2 = new Token(tokenName, color2);

        assertThat(token1, is(not(equalTo((token2)))));
    }


}
