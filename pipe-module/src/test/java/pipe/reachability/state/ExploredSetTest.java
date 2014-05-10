package pipe.reachability.state;

import org.junit.Test;
import pipe.animation.HashedState;
import pipe.animation.State;

import java.util.Arrays;
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
        set = new ExploredSet(5);

    }

    /**
     * @param p1Tokens
     * @param p2Tokens
     * @return State representation of a state with two places P1, P2 with the specified number of Default tokens
     */
    public ExplorerState createState(int p1Tokens, int p2Tokens) {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", p1Tokens);
        tokens.put("P2", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", p2Tokens);
        State state = new HashedState(tokens);
        return HashedExplorerState.tangibleState(state);

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

    @Test
    public void addAll() {
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        assertTrue(set.contains(explorerState));
        assertTrue(set.contains(other));
        assertTrue(set.contains(another));
    }

    @Test
    public void clear() {
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        set.clear();
        assertFalse(set.contains(explorerState));
        assertFalse(set.contains(another));
        assertFalse(set.contains(explorerState));
    }

    @Test
    public void allAllOfOtherSet() {
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        assertTrue(set.contains(explorerState));
        assertTrue(set.contains(another));
        assertTrue(set.contains(other));


        ExplorerState other2 = createState(6, 0);
        ExplorerState another2 = createState(1, 5);
        ExploredSet newSet = new ExploredSet(10);
        newSet.add(other2);
        newSet.add(another2);

        set.addAll(newSet);
        assertTrue(set.contains(another2));
        assertTrue(set.contains(other2));
    }
}