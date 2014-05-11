package pipe.animation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class HashedStateTest {

    State state;

    @Before
    public void setUp() {
        Multimap<String, TokenCount> map = HashMultimap.create();
        map.put("P1", new TokenCount("Default", 2));
        state = new HashedState(map);
    }

    @Test
    public void multiMapTest() {

        Multimap<String, TokenCount> map = HashMultimap.create();
        map.put("P1", new TokenCount("Default", 2));
        assertThat(map.get("P1")).contains(new TokenCount("Default", 2));

        Multimap<String, TokenCount> map2 = HashMultimap.create();
        map2.putAll(map);
        assertThat(map2.get("P1")).contains(new TokenCount("Default", 2));

    }

    @Test
    public void twoDefaultTokensP1() {
        assertThat(state.getTokens("P1")).contains(new TokenCount("Default", 2));
    }

    @Test
    public void stateToString() {
       assertEquals("{\"P1\": {\"Default\": 2}}", state.toString());
    }
}