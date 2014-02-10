package pipe.dsl;

import org.junit.Test;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class APetriNetTest {
    @Test
    public void createsPetriNetWithOnePlace() {
        PetriNet petriNet = APetriNet.withOnly(APlace.withId("P0"));

        PetriNet expected = new PetriNet();
        Place place = new Place("P0", "P0");
        expected.addPlace(place);

        assertEquals(expected, petriNet);
    }

    @Test
    public void createsPetriNetWithMultipleItems() {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.RED))
                                     .and(APlace.withId("P0"))
                                     .and(ATransition.withId("T0"))
                                     .andFinally(ANormalArc.withSource("P0").andTarget("T0").with("5", "Red").tokens());

        PetriNet expected = new PetriNet();
        Token token = new Token("Default", true, 0, Color.RED);
        expected.addToken(token);
        Place place = new Place("P0", "P0");
        expected.addPlace(place);
        Transition transition = new Transition("T0", "T0");
        expected.addTransition(transition);
        Map<Token, String> arcWeights = new HashMap<>();
        arcWeights.put(token, "5");
        Arc<Place, Transition> arc = new Arc<>(place, transition, arcWeights, ArcType.NORMAL);
        expected.addArc(arc);

        assertEquals(expected, petriNet);
    }

}
