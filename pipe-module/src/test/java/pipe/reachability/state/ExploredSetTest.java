package pipe.reachability.state;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import pipe.animation.HashedState;
import pipe.animation.State;
import pipe.animation.TokenCount;
import pipe.exceptions.PetriNetComponentNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class ExploredSetTest {

    ExplorerState explorerState;

    ExploredSet set;

    @org.junit.Before
    public void setUp() {

        explorerState = createState(1, 2);

    }

    /**
     * @param p1Tokens
     * @param p2Tokens
     * @return State representation of a state with two places P1, P2 with the specified number of Default tokens
     */
    public ExplorerState createState(int p1Tokens, int p2Tokens) {
        Multimap<String,TokenCount> tokens = HashMultimap.create();
        tokens.put("P1", new TokenCount("Default", p1Tokens));
        tokens.put("P2", new TokenCount("Default", p2Tokens));
        State state = new HashedState(tokens);
        return HashedExplorerState.tangibleState(state);

    }

    @Test
    public void containsEmpty() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        assertFalse(set.contains(explorerState));
    }

    @Test
    public void containsExactItem() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        set.add(explorerState);
        assertTrue(set.contains(explorerState));
    }

    @Test
    public void containsDuplicateItem() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        set.add(explorerState);
        ExplorerState sameState = createState(1, 2);
        assertTrue(set.contains(sameState));
    }

    @Test
    public void containsDuplicateItemOppositeOrderHashMap() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        set.add(explorerState);
        ExplorerState sameState = createStateOppositeOrder(1, 2);
        assertTrue(set.contains(sameState));
    }

    /**
     * @param p1Tokens
     * @param p2Tokens
     * @return State representation of a state with two places P1, P2 with the specified number of Default tokens
     */
    public ExplorerState createStateOppositeOrder(int p1Tokens, int p2Tokens) {
        Multimap<String,TokenCount> tokens = HashMultimap.create();
        tokens.put("P2", new TokenCount("Default", p2Tokens));
        tokens.put("P1", new TokenCount("Default", p1Tokens));
        State state = new HashedState(tokens);
        return HashedExplorerState.tangibleState(state);

    }

    @Test
    public void doesNotContainDifferentItem() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        set.add(explorerState);
        ExplorerState differentState = createState(22, 1);
        assertFalse(set.contains(differentState));
    }

    @Test
    public void addAll() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        assertTrue(set.contains(explorerState));
        assertTrue(set.contains(other));
        assertTrue(set.contains(another));
    }

    @Test
    public void clear() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        set.clear();
        assertFalse(set.contains(explorerState));
        assertFalse(set.contains(another));
        assertFalse(set.contains(explorerState));
    }

    @Test
    public void allAllOfOtherSetSameSize() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        assertTrue(set.contains(explorerState));
        assertTrue(set.contains(another));
        assertTrue(set.contains(other));


        ExplorerState other2 = createState(6, 0);
        ExplorerState another2 = createState(1, 5);
        ExploredSet newSet = new ExploredSet(10, Arrays.asList("P1", "P2"));
        newSet.add(other2);
        newSet.add(another2);

        set.addAll(newSet);
        assertTrue(set.contains(another2));
        assertTrue(set.contains(other2));
    }

    @Test
    public void addAllOfDifferentSizeThrowsError() {
        set = new ExploredSet(10, Arrays.asList("P1", "P2"));
        ExplorerState other = createState(2, 10);
        ExplorerState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another));
        assertTrue(set.contains(explorerState));
        assertTrue(set.contains(another));
        assertTrue(set.contains(other));


        ExplorerState other2 = createState(6, 0);
        ExplorerState another2 = createState(1, 5);
        ExploredSet newSet = new ExploredSet(5, Arrays.asList("P1", "P2"));
        newSet.add(other2);
        newSet.add(another2);

        try {
            set.addAll(newSet);
            fail("Expected Runtime excption because sets differed in size and there is no item to reconstruct the hash code from!");
        } catch (RuntimeException e) {
            assertEquals("Cannot combine sets with different sized arrays. Due to compression here is no item to reconstruct hashcode from!", e.getMessage());
        }
    }

    /**
     * This test was found when exploring medium_complex_5832,
     * ExploredSet was returning false for containing the item
     * when it had been added to the state
     */
    @Test
    public void containsLargeState() throws IOException, PetriNetComponentNotFoundException {
        String jsonValue =
                "{\"P12\": {\"Default\": 0}, \"P11\": {\"Default\": 1}, \"P14\": {\"Default\": 1}, \"P13\": {\"Default\": 0}, \"P9\": {\"Default\": 0}, \"P16\": {\"Default\": 1}, \"P15\": {\"Default\": 0}, \"P8\": {\"Default\": 1}, \"P17\": {\"Default\": 0}, \"P5\": {\"Default\": 0}, \"P4\": {\"Default\": 0}, \"P7\": {\"Default\": 0}, \"P6\": {\"Default\": 0}, \"P1\": {\"Default\": 0}, \"P0\": {\"Default\": 1}, \"P3\": {\"Default\": 0}, \"P2\": {\"Default\": 1}, \"P10\": {\"Default\": 0}}";
        ExplorerState state = StateUtils.toState(jsonValue);

        set = new ExploredSet(10, new LinkedList<>(state.getState().asMap().keySet()));
        set.add(state);
        ExplorerState sameState = StateUtils.toState(jsonValue);
        assertTrue(set.contains(sameState));
    }

    /**
     * This test was added because during debugging these two states cause problems
     * for the contains method
     * @throws IOException
     */
    @Test
    public void similarStates() throws IOException {
        ExplorerState state = StateUtils.toState("{\"P1\": {\"Default\": 1}, \"P0\": {\"Default\": 0}, \"P3\": {\"Default\": 0}, \"P2\": {\"Default\": 1}}");
        ExplorerState state2 = StateUtils.toState("{\"P1\": {\"Default\": 0}, \"P0\": {\"Default\": 1}, \"P3\": {\"Default\": 1}, \"P2\": {\"Default\": 0}}");
        set = new ExploredSet(10, new LinkedList<>(state.getState().asMap().keySet()));
        set.add(state);
        assertFalse(set.contains(state2));
    }

}