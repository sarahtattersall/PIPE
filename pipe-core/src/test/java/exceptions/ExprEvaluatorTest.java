package exceptions;

import net.sourceforge.jeval.EvaluationException;
import org.junit.Before;
import org.junit.Test;
import pipe.models.petrinet.ExprEvaluator;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import utils.TokenUtils;

import static org.junit.Assert.assertTrue;

public class ExprEvaluatorTest {

    ExprEvaluator exprEvaluator;

    PetriNet net;

    @Before
    public void setUp() {
        net = new PetriNet();
        exprEvaluator = new ExprEvaluator(net);
    }

    @Test
    public void evaluatesCorrectCapacityWhenExpressionIsTrue() throws EvaluationException {
        Place place = new Place("P0", "P0");
        place.setCapacity(2);

        net.addPlace(place);

        double result = exprEvaluator.parseAndEvalExprForTransition("cap(P0) <= 2");

        assertTrue("cap(P0) <= is false", isTrue(result));
    }

    @Test
    public void evaluatesCorrectCapacityWhenExpressionIsFalse() throws EvaluationException {
        Place place = new Place("P0", "P0");
        place.setCapacity(2);

        net.addPlace(place);
        double result = exprEvaluator.parseAndEvalExprForTransition("cap(P0) != 2");

        assertTrue("cap(P0) != 2 is true", isFalse(result));
    }


    @Test
    public void evaluatesCorrectTokensWhenExpressionIsTrue() throws EvaluationException {
        Token defaultToken = TokenUtils.createDefaultToken();
        net.addToken(defaultToken);

        Place place = new Place("P0", "P0");
        int tokenCount = 2;
        place.setTokenCount(defaultToken, tokenCount);

        net.addPlace(place);
        double result = exprEvaluator.parseAndEvalExprForTransition("#(P0) <= " + tokenCount);

        assertTrue("#(P0) <= 2 is false", isTrue(result));
    }

    @Test
    public void evaluatesCorrectTokensWhenExpressionIsFalse() throws EvaluationException {
        Token defaultToken = TokenUtils.createDefaultToken();
        net.addToken(defaultToken);

        Place place = new Place("P0", "P0");
        int tokenCount = 2;
        place.setTokenCount(defaultToken, tokenCount);

        net.addPlace(place);
        double result = exprEvaluator.parseAndEvalExprForTransition("#(P0) != " + tokenCount);

        assertTrue("#(P0) != 2 is true", isFalse(result));
    }


    boolean isTrue(double value) {
        return value == 1.0;
    }

    boolean isFalse(double value) {
        return value == 0.0;
    }
}
