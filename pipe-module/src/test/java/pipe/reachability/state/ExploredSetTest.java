package pipe.reachability.state;

import org.junit.Test;
import pipe.animation.HashedState;
import pipe.animation.State;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExploredSetTest {

    ExplorerState explorerState;

    ExploredSet set;
    @org.junit.Before
    public void setUp() {

        explorerState = createState(1, 2);
        set = new ExploredSet();

    }

    @Test
    public void containsEmpty() {
        assertFalse(set.contains(explorerState));
    }

    @Test
    public void containsExactItem() {
        set.add(explorerState);
        assertTrue(set.contains(explorerState));
    }

    @Test
    public void containsDuplicateItem() {
        set.add(explorerState);
        ExplorerState sameState = createState(1, 2);
        assertTrue(set.contains(sameState));
    }


    @Test
    public void doesNotContainDifferentItem() {
        set.add(explorerState);
        ExplorerState sameState = createState(2, 1);
        assertFalse(set.contains(sameState));
    }

    public ExplorerState createState(int p1Tokens, int p2Tokens) {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", p1Tokens);
        tokens.put("P2", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", p2Tokens);
        State state = new HashedState(tokens);
        return HashedExplorerState.tangibleState(state);

    }
}