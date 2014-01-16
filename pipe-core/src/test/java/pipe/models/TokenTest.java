package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.exceptions.TokenLockedException;
import pipe.models.component.token.Token;

import java.awt.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
        Token token1 = new Token(tokenName, false, 0, sameColor);
        Token token2 = new Token(tokenName, true, 1, sameColor);

        assertEquals(token1, token2);
    }

    @Test
    public void tokenNameInequality() {
        String tokenName = "Default";
        Color sameColor = new Color(0, 0, 0);
        Token token1 = new Token(tokenName, false, 0, sameColor);
        Token token2 = new Token(tokenName + "different", true, 1, sameColor);

        assertThat(token1, is(not(equalTo((token2)))));
    }


    @Test
    public void tokenColorInequality() {
        String tokenName = "Default";
        Color color1 = new Color(0, 0, 0);
        Token token1 = new Token(tokenName, false, 0, color1);
        Color color2 = new Color(255, 255, 1);
        Token token2 = new Token(tokenName, true, 1, color2);

        assertThat(token1, is(not(equalTo((token2)))));
    }


}
