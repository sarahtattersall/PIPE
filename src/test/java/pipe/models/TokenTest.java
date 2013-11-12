package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.exceptions.TokenLockedException;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TokenTest {

    Token token;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        token = new Token();
    }

    @Test
    public void testSetEnabledThrowsErrorIfLocked() throws Exception {
        token.incrementLock();

        exception.expect(TokenLockedException.class);
        token.setEnabled(true);
    }

    @Test
    public void incrementLockTokenKnowsItsLocked() {
        assertFalse(token.isLocked());
        token.incrementLock();
        assertTrue(token.isLocked());
    }

    @Test
    public void decrementLockUnlocksToken() {
        assertFalse(token.isLocked());
        token.incrementLock();
        token.decrementLock();
        assertFalse(token.isLocked());
    }

    @Test
    public void decrementLockTokenKnowsItsLocked() {
        assertFalse(token.isLocked());
        token.incrementLock();
        token.incrementLock();
        token.decrementLock();
        assertTrue(token.isLocked());
    }

    @Test
    public void settinLockCountZeroDoesNotLockToken() {
        token.setLockCount(0);
        assertFalse(token.isLocked());
    }

    @Test
    public void settingLockCountGreaterThanZeroLocksToken() {
        token.setLockCount(4);
        assertTrue(token.isLocked());
    }

    /**
     * Test tokens are compared via their tokenName and color
     */
    @Test
    public void tokenEquality() {
        String tokenName = "Default";
        Color sameColor = new Color(0,0,0);
        Token token1 = new Token(tokenName, false, 0, sameColor);
        Token token2 = new Token(tokenName, true, 1, sameColor);

        assertEquals(token1, token2);
    }

    @Test
    public void tokenNameInequality() {
        String tokenName = "Default";
        Color sameColor = new Color(0,0,0);
        Token token1 = new Token(tokenName, false, 0, sameColor);
        Token token2 = new Token(tokenName + "different", true, 1, sameColor);

        assertThat(token1, is(not(equalTo((token2)))));
    }


    @Test
    public void tokenColorInequality() {
        String tokenName = "Default";
        Color color1 = new Color(0,0,0);
        Token token1 = new Token(tokenName, false, 0, color1);
        Color color2 = new Color(255, 255, 1);
        Token token2 = new Token(tokenName, true, 1, color2);

        assertThat(token1, is(not(equalTo((token2)))));
    }



}
