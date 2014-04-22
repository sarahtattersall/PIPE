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


     boolean isTrue(double value) {
        return value == 1.0;
    }

    boolean isFalse(double value) {
        return value == 0.0;
    }
}
