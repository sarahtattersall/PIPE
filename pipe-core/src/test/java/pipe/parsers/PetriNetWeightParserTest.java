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

public class PetriNetWeightParserTest {

    private static final PetriNet EMPTY_PETRI_NET = new PetriNet();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void correctlyIdentifiesErrors() {
        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(EMPTY_PETRI_NET);
        FunctionalResults<Double> result = parser.evaluateExpression("2 +");
        assertTrue(result.hasErrors());
    }

    @Test
    public void producesCorrectErrorMessage() {
        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(EMPTY_PETRI_NET);
        FunctionalResults<Double> result = parser.evaluateExpression("2 *");
        assertThat(result.getErrors()).containsExactly("line 1:3 no viable alternative at input '<EOF>'");
    }

    @Test
    public void expressionIsNegativeIfContainsErrors() throws UnparsableException {
        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(EMPTY_PETRI_NET);
        FunctionalResults<Double> result = parser.evaluateExpression("2 *");
        assertEquals(new Double(-1.), result.getResult());
    }


    @Test
    public void returnsErrorIfResultIsLessThanZero() throws UnparsableException {
        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(EMPTY_PETRI_NET);
        FunctionalResults<Double> result = parser.evaluateExpression("2 - 6");
        assertThat(result.getErrors()).containsExactly("Expression result cannot be less than zero!");
    }


    @Test
    public void willNotEvaluateExpressionIfPetriNetDoesNotContainComponent() throws UnparsableException {
        PetriNet petriNet = APetriNet.withOnly(APlace.withId("P1"));
        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(petriNet);
        FunctionalResults<Double> result = parser.evaluateExpression("#(P0)");
        assertThat(result.getErrors()).contains("Not all referenced components exist in the Petri net!");
    }



    @Test
    public void evaluatesIfPlaceIsInPetriNet() throws UnparsableException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(APlace.withId("P0").containing(10, "Default").tokens());

        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(petriNet);
        FunctionalResults<Double> result = parser.evaluateExpression("#(P0)");
        assertEquals(new Double(10), result.getResult());
    }

    @Test
    public void returnsCorrectComponentsForTotalTokens() {

        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(APlace.withId("P0").containing(10, "Default").tokens());

        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(petriNet);
        FunctionalResults<Double> result = parser.evaluateExpression("#(P0)");
        assertTrue(result.getComponents().contains("P0"));
    }


    @Test
    public void returnsCorrectComponentsForSpecificTokens() {

        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).andFinally(APlace.withId("P0").containing(10, "Default").tokens());

        FunctionalWeightParser<Double> parser = new PetriNetWeightParser(petriNet);
        FunctionalResults<Double> result = parser.evaluateExpression("#(P0, Default)");
        assertTrue(result.getComponents().contains("P0"));
        assertTrue(result.getComponents().contains("Default"));
    }


}
