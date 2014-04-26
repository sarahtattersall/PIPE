package pipe.reachability;

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
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ReachabilityTest {
    WriterFormatter formatter;
    ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        formatter = new PrettyWriterFormatter();
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
        verifyOutput(outputToInputStream(outputStream), "{P1: 0, P0: 1} to {P1: 1, P0: 0} with rate 1.000000",
                                                        "{P1: 1, P0: 0} to {P1: 0, P0: 1} with rate 1.000000");
    }

    @Test
    public void simpleVanishingState()
            throws JAXBException, UnparsableException, PetriNetComponentNotFoundException, IOException {
        PetriNet petriNet = Utils.readPetriNet("/simple_vanishing.xml");
        Token token = petriNet.getComponent("Default", Token.class);
        Reachability reachability = new Reachability(petriNet, token, formatter);
        reachability.generate(outputStream);
        verifyOutput(outputToInputStream(outputStream),
                "{3: 0, 2: 0, 1: 1, 7: 0, 6: 0, 5: 0, 4: 0, 8: 0} to {3: 0, 2: 0, 1: 0, 7: 0, 6: 1, 5: 0, 4: 0, 8: 0} with rate 3.000000",
                "{3: 0, 2: 0, 1: 1, 7: 0, 6: 0, 5: 0, 4: 0, 8: 0} to {3: 0, 2: 0, 1: 0, 7: 0, 6: 0, 5: 1, 4: 0, 8: 0} with rate 3.750000",
                "{3: 0, 2: 0, 1: 1, 7: 0, 6: 0, 5: 0, 4: 0, 8: 0} to {3: 0, 2: 0, 1: 0, 7: 1, 6: 0, 5: 0, 4: 0, 8: 0} with rate 0.750000",
                "{3: 0, 2: 0, 1: 1, 7: 0, 6: 0, 5: 0, 4: 0, 8: 0} to {3: 0, 2: 0, 1: 0, 7: 0, 6: 0, 5: 0, 4: 0, 8: 1} with rate 0.500000");
    }

    public InputStream outputToInputStream(ByteArrayOutputStream stream) {
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public void verifyOutput(InputStream inputStream, String... values) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        for (String value : values) {
            line = in.readLine();
            assertNotNull(line);
            assertEquals(value, line);
        }
    }

}
