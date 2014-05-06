package pipe.reachability.state;

import org.junit.Test;
import pipe.animation.HashedState;
import pipe.animation.State;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HashedExplorerStateTest {

    @Test
    public void equalityIdenticalState() {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", 3);
        State state = new HashedState(tokens);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.tangibleState(state);
        assertEquals(explorerState1, explorerState2);
    }



    @Test
    public void equalitySameState() {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", 3);
        State state = new HashedState(tokens);


        Map<String, Map<String, Integer>> tokens2 = new HashMap<>();
        tokens2.put("P1", new HashMap<String, Integer>());
        tokens2.get("P1").put("Default", 3);
        State state2 = new HashedState(tokens2);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.tangibleState(state2);
        assertEquals(explorerState1, explorerState2);
    }

    @Test
    public void inequalityIdenticalState() {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", 3);
        State state = new HashedState(tokens);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.vanishingState(state);
        assertThat(explorerState1, is(not(explorerState2)));
    }


    @Test
    public void inequalitySameState() {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", 3);
        State state = new HashedState(tokens);


        Map<String, Map<String, Integer>> tokens2 = new HashMap<>();
        tokens2.put("P1", new HashMap<String, Integer>());
        tokens2.get("P1").put("Default", 3);
        State state2 = new HashedState(tokens2);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.vanishingState(state2);
        assertThat(explorerState1, is(not(explorerState2)));
    }

    @Test
    public void inequalityDifferentState() {
        Map<String, Map<String, Integer>> tokens = new HashMap<>();
        tokens.put("P1", new HashMap<String, Integer>());
        tokens.get("P1").put("Default", 3);
        State state = new HashedState(tokens);


        Map<String, Map<String, Integer>> tokens2 = new HashMap<>();
        tokens2.put("P1", new HashMap<String, Integer>());
        tokens2.get("P1").put("Red", 3);
        State state2 = new HashedState(tokens2);

        ExplorerState explorerState1 = HashedExplorerState.tangibleState(state);
        ExplorerState explorerState2 = HashedExplorerState.tangibleState(state2);
        assertThat(explorerState1, is(not(explorerState2)));
    }

}