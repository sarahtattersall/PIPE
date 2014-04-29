package pipe.reachability;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.token.Token;
import pipe.reachability.io.ByteWriterFormatter;
import pipe.reachability.state.HashedState;
import pipe.reachability.state.Record;
import pipe.reachability.state.State;

import java.awt.Color;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ByteWriterFormatterTest {
    ByteWriterFormatter formatter;

    State state;

    State successor;

    double rate;

    @Before
    public void setUp() throws IOException {
        formatter = new ByteWriterFormatter();

        Map<String, Map<Token, Integer>> stateTokens = new HashMap<>();
        Token defaultToken = new Token("Default", Color.BLACK);
        stateTokens.put("P1", new HashMap<Token, Integer>());
        stateTokens.get("P1").put(defaultToken, 1);
        stateTokens.put("P2", new HashMap<Token, Integer>());
        stateTokens.get("P2").put(defaultToken, 2);
        state = HashedState.tangibleState(stateTokens);


        Map<String, Map<Token, Integer>> successorTokens = new HashMap<>();
        successorTokens.put("P1", new HashMap<Token, Integer>());
        successorTokens.get("P1").put(defaultToken, 0);
        successorTokens.put("P2", new HashMap<Token, Integer>());
        successorTokens.get("P2").put(defaultToken, 3);
        successor = HashedState.vanishingState(successorTokens);

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
