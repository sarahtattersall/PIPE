package pipe.reachability;

import org.junit.Before;
import org.junit.Test;
import pipe.animation.HashedState;
import pipe.reachability.io.ByteWriterFormatter;
import pipe.reachability.state.ExplorerState;
import pipe.reachability.state.HashedExplorerState;
import pipe.reachability.state.Record;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ByteWriterFormatterTest {
    ByteWriterFormatter formatter;

    ExplorerState state;

    ExplorerState successor;

    double rate;

    @Before
    public void setUp() throws IOException {
        formatter = new ByteWriterFormatter();

        Map<String, Map<String, Integer>> stateTokens = new HashMap<>();
        String defaultToken = "Default";
        stateTokens.put("P1", new HashMap<String, Integer>());
        stateTokens.get("P1").put(defaultToken, 1);
        stateTokens.put("P2", new HashMap<String, Integer>());
        stateTokens.get("P2").put(defaultToken, 2);
        state = HashedExplorerState.tangibleState(new HashedState(stateTokens));


        Map<String, Map<String, Integer>> successorTokens = new HashMap<>();
        successorTokens.put("P1", new HashMap<String, Integer>());
        successorTokens.get("P1").put(defaultToken, 0);
        successorTokens.put("P2", new HashMap<String, Integer>());
        successorTokens.get("P2").put(defaultToken, 3);
        successor = HashedExplorerState.vanishingState(new HashedState(successorTokens));

        rate = 4.5;
    }

    @Test
    public void correctlySerializesAndDeserializes() throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(stream)) {
            formatter.write(state, successor, rate, outputStream);


            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 ObjectInputStream inputStream = new ObjectInputStream(s)) {
                Record record = formatter.read(inputStream);
                assertEquals(state, record.state);
                assertEquals(successor, record.successor);
                assertEquals(rate, record.rate, 0.0001);
            }
        }
    }

    @Test
    public void correctlySerializesAndDeserializesTwoObjects() throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(stream)) {
            formatter.write(state, successor, rate, outputStream);
            formatter.write(state, successor, rate, outputStream);


            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 ObjectInputStream inputStream = new ObjectInputStream(s)) {
                for (int i = 0; i < 1; i++) {
                    Record record = formatter.read(inputStream);
                    assertEquals(state, record.state);
                    assertEquals(successor, record.successor);
                    assertEquals(rate, record.rate, 0.0001);
                }
            }
        }
    }
}
