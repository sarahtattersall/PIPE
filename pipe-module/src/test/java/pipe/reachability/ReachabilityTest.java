package pipe.reachability;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.dsl.*;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.token.Token;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;
import utils.Utils;

import javax.xml.bind.JAXBException;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ReachabilityTest {
    WriterFormatter formatter;
    ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        formatter = new ByteWriterFormatter();
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void simpleCyclic() throws PetriNetComponentNotFoundException, IOException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P0").and(1, "Default").token()).and(APlace.withId("P1")).and(
                ATransition.withId("T0").whichIsTimed()).and(ATransition.withId("T1").whichIsTimed())
                .and(ANormalArc.withSource("P0").andTarget("T0").with("1", "Default").token())
                .and(ANormalArc.withSource("T0").andTarget("P1").with("1", "Default").token())
                .and(ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token())
                .andFinally(ANormalArc.withSource("T1").andTarget("P0").with("1", "Default").token());
        Token token = petriNet.getComponent("Default", Token.class);

        Reachability reachability = new Reachability(petriNet, token, formatter);
        reachability.generate(outputStream);
        verifyOutput(outputToInputStream(outputStream), createRecord("{\"P1\": 0, \"P0\": 1}", "{\"P1\": 1, \"P0\": 0}", 1.0),
                                                        createRecord("{\"P1\": 1, \"P0\": 0}", "{\"P1\": 0, \"P0\": 1}", 1.0));
    }

    @Test
    public void simpleVanishingState()
            throws JAXBException, UnparsableException, PetriNetComponentNotFoundException, IOException {
        PetriNet petriNet = Utils.readPetriNet("/simple_vanishing.xml");
        Token token = petriNet.getComponent("Default", Token.class);
        Reachability reachability = new Reachability(petriNet, token, formatter);
        reachability.generate(outputStream);
        verifyOutput(outputToInputStream(outputStream),
                createRecord("{\"3\": 0, \"2\": 0, \"1\": 1, \"7\": 0, \"6\": 0, \"5\": 0, \"4\": 0, \"8\": 0}", "{\"3\": 0, \"2\": 0, \"1\": 0, \"7\": 0, \"6\": 1, \"5\": 0, \"4\": 0, \"8\": 0}", 3.00),
                createRecord("{\"3\": 0, \"2\": 0, \"1\": 1, \"7\": 0, \"6\": 0, \"5\": 0, \"4\": 0, \"8\": 0}", "{\"3\": 0, \"2\": 0, \"1\": 0, \"7\": 0, \"6\": 0, \"5\": 1, \"4\": 0, \"8\": 0}", 3.75),
                createRecord("{\"3\": 0, \"2\": 0, \"1\": 1, \"7\": 0, \"6\": 0, \"5\": 0, \"4\": 0, \"8\": 0}", "{\"3\": 0, \"2\": 0, \"1\": 0, \"7\": 1, \"6\": 0, \"5\": 0, \"4\": 0, \"8\": 0}", 0.75),
                createRecord("{\"3\": 0, \"2\": 0, \"1\": 1, \"7\": 0, \"6\": 0, \"5\": 0, \"4\": 0, \"8\": 0}", "{\"3\": 0, \"2\": 0, \"1\": 0, \"7\": 0, \"6\": 0, \"5\": 0, \"4\": 0, \"8\": 1}", 0.50));
    }

    public InputStream outputToInputStream(ByteArrayOutputStream stream) {
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public void verifyOutput(InputStream inputStream, Record... records) throws IOException {
        for (Record expected : records) {
            Record actual = formatter.read(inputStream);
            assertEquals(expected, actual);
        }
    }

    private Map<String, Integer> toMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<HashMap<String, Integer>>(){});
    }

    private Record createRecord(String jsonState, String jsonSuccessor, double rate)
            throws IOException {
        State state = new HashedState(toMap(jsonState));
        State successor = new HashedState(toMap(jsonSuccessor));
        return new Record(state, successor, rate);
    }

}
