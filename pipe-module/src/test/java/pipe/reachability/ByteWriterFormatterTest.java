package pipe.reachability;

import org.junit.Before;
import org.junit.Test;
import pipe.reachability.state.HashedState;
import pipe.reachability.state.Record;
import pipe.reachability.state.State;
import pipe.reachability.io.ByteWriterFormatter;

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

        Map<String, Integer> stateTokens = new HashMap<>();
        stateTokens.put("P1", 1);
        stateTokens.put("P2", 2);
        state = new HashedState(stateTokens);


        Map<String, Integer> successorTokens = new HashMap<>();
        successorTokens.put("P1", 0);
        successorTokens.put("P2", 3);
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
