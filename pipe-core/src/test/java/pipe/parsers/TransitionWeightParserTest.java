package pipe.parsers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.dsl.APetriNet;
import pipe.dsl.APlace;
import pipe.dsl.AToken;
import pipe.models.petrinet.PetriNet;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransitionWeightParserTest {

    private static final PetriNet EMPTY_PETRI_NET = new PetriNet();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void correctlyIdentifiesErrors() {
        FunctionalWeightParser parser = new TransitionWeightParser(EMPTY_PETRI_NET, "2 + ");
        assertTrue(parser.containsErrors());
    }

    @Test
    public void producesCorrectErrorMessage() {
        FunctionalWeightParser parser = new TransitionWeightParser(EMPTY_PETRI_NET, "2 *");
        assertThat(parser.getErrors()).containsExactly("line 1:3 no viable alternative at input '<EOF>'");
    }

    @Test
    public void willNotEvaluateExpressionIfContainsErrors() throws UnparsableException {
        FunctionalWeightParser parser = new TransitionWeightParser(EMPTY_PETRI_NET, "2 *");

        expectedException.expect(UnparsableException.class);
        expectedException.expectMessage("There were errors in parsing the expression, cannot calculate value!");

        parser.evaluateExpression();
    }

    @Test
    public void willNotEvaluateExpressionIfPetriNetDoesNotContainComponent() throws UnparsableException {
        PetriNet petriNet = APetriNet.withOnly(APlace.withId("P1"));
        FunctionalWeightParser parser = new TransitionWeightParser(petriNet, "#(P0)");
        assertThat(parser.getErrors()).contains("Not all referenced components exist in the Petri net!");
    }



    @Test
    public void evaluatesIfPlaceIsInPetriNet() throws UnparsableException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(APlace.withId("P0").containing(10, "Default").tokens());

        FunctionalWeightParser parser = new TransitionWeightParser(petriNet, "#(P0)");

        Double value = parser.evaluateExpression();
        assertEquals(new Double(10), value);
    }


}
