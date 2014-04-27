package pipe.reachability;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.token.Token;
import pipe.reachability.state.HashedState;
import pipe.reachability.state.Record;
import pipe.reachability.state.State;
import pipe.reachability.io.ByteWriterFormatter;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ByteWriterFormatterTest {
    ByteWriterFormatter formatter;
    ByteArrayOutputStream stream;
    State state;
    State successor;
    double rate;

    @Before
    public void setUp() {
        formatter = new ByteWriterFormatter();
        stream = new ByteArrayOutputStream();

        Map<String, Map<Token, Integer>> stateTokens = new HashMap<>();
        Token defaultToken = new Token("Default", Color.BLACK);
        stateTokens.put("P1", new HashMap<Token, Integer>());
        stateTokens.get("P1").put(defaultToken, 1);
        stateTokens.put("P2", new HashMap<Token, Integer>());
        stateTokens.get("P2").put(defaultToken, 2);
        state = new HashedState(stateTokens);


        Map<String, Map<Token, Integer>> successorTokens = new HashMap<>();
        successorTokens.put("P1", new HashMap<Token, Integer>());
        successorTokens.get("P1").put(defaultToken, 0);
        successorTokens.put("P2", new HashMap<Token, Integer>());
        successorTokens.get("P2").put(defaultToken, 3);
        successor = new HashedState(successorTokens);

        rate = 4.5;
    }

    @Test
    public void correctlySerializesAndDeserializes() throws IOException {
        formatter.write(state, successor, rate, stream);
        stream.close();
        Record record = formatter.read(new ByteArrayInputStream(stream.toByteArray()));
        assertEquals(state, record.state);
        assertEquals(successor, record.successor);
        assertEquals(rate, record.rate, 0.0001);
    }
}
