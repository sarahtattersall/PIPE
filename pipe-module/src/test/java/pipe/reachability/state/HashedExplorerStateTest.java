package pipe.reachability.state;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import pipe.animation.HashedState;
import pipe.animation.State;
import pipe.animation.TokenCount;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HashedExplorerStateTest {

    @Test
    public void equalityIdenticalState() {
        Multimap<String, TokenCount> tokens = HashMultimap.create();
        tokens.put("P1", new TokenCount("Default", 3));
        State state = new HashedState(tokens);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.tangibleState(state);
        assertEquals(explorerState1, explorerState2);
    }



    @Test
    public void equalitySameState() {
        Multimap<String, TokenCount> tokens = HashMultimap.create();
        tokens.put("P1", new TokenCount("Default", 3));
        State state = new HashedState(tokens);


        Multimap<String, TokenCount> tokens2 = HashMultimap.create();
        tokens2.put("P1", new TokenCount("Default", 3));
        State state2 = new HashedState(tokens2);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.tangibleState(state2);
        assertEquals(explorerState1, explorerState2);
    }

    @Test
    public void inequalityIdenticalState() {
        Multimap<String, TokenCount> tokens = HashMultimap.create();
        tokens.put("P1", new TokenCount("Default", 3));
        State state = new HashedState(tokens);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.vanishingState(state);
        assertThat(explorerState1, is(not(explorerState2)));
    }


    @Test
    public void inequalitySameState() {
        Multimap<String, TokenCount> tokens = HashMultimap.create();
        tokens.put("P1", new TokenCount("Default", 3));
        State state = new HashedState(tokens);


        Multimap<String, TokenCount> tokens2 = HashMultimap.create();
        tokens2.put("P1", new TokenCount("Default", 3));
        State state2 = new HashedState(tokens2);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.vanishingState(state2);
        assertThat(explorerState1, is(not(explorerState2)));
    }

    @Test
    public void inequalityDifferentState() {
        Multimap<String, TokenCount> tokens = HashMultimap.create();
        tokens.put("P1", new TokenCount("Default", 3));
        State state = new HashedState(tokens);

        Multimap<String, TokenCount> tokens2 = HashMultimap.create();
        tokens2.put("P1", new TokenCount("Red", 3));
        State state2 = new HashedState(tokens2);
        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.tangibleState(state2);
        assertThat(explorerState1, is(not(explorerState2)));
    }

}