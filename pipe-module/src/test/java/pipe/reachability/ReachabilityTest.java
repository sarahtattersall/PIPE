package pipe.reachability;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.dsl.*;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.token.Token;
import pipe.models.petrinet.PetriNet;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReachabilityTest {
    @Mock
    Writer mockWriter;


    @Test
    public void foo() throws PetriNetComponentNotFoundException, IOException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P0").and(1, "Default").token()).and(APlace.withId("P1")).and(
                ATransition.withId("T0").whichIsTimed()).and(ATransition.withId("T1").whichIsTimed())
                .and(ANormalArc.withSource("P0").andTarget("T0").with("1", "Default").token())
                .and(ANormalArc.withSource("T0").andTarget("P1").with("1", "Default").token())
                .and(ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token())
                .andFinally(ANormalArc.withSource("T1").andTarget("P0").with("1", "Default").token());
        Token token = petriNet.getComponent("Default", Token.class);

        Reachability reachability = new Reachability(petriNet, token, new PrettyWriterFormatter());
        reachability.generate(mockWriter);
        verify(mockWriter).write("{P1: 0, P0: 1} to {P1: 1, P0: 0} with rate 1.000000\n");
        verify(mockWriter).write("{P1: 1, P0: 0} to {P1: 0, P0: 1} with rate 1.000000\n");
    }

}
