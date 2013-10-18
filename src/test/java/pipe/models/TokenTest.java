package pipe.models;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.exceptions.TokenLockedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
