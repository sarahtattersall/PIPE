package pipe.reachability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;
import pipe.animation.HashedState;
import pipe.animation.TokenCount;
import pipe.reachability.io.ByteWriterFormatter;
import pipe.reachability.state.ExplorerState;
import pipe.reachability.state.HashedExplorerState;
import pipe.reachability.state.Record;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ByteWriterFormatterTest {
    ByteWriterFormatter formatter;

    ExplorerState state;

    ExplorerState successor;

    double rate;

    @Before
    public void setUp() throws IOException {
        formatter = new ByteWriterFormatter();

        Multimap<String, TokenCount> stateTokens = HashMultimap.create();
        stateTokens.put("P1", new TokenCount("Default", 1));
        stateTokens.put("P2", new TokenCount("Default", 2));

        state = HashedExplorerState.tangibleState(new HashedState(stateTokens));



        Multimap<String, TokenCount> successorTokens = HashMultimap.create();
        stateTokens.put("P1", new TokenCount("Default", 0));
        stateTokens.put("P2", new TokenCount("Default", 3));
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
