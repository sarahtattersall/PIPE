package pipe.parsers;

import pipe.models.petrinet.PetriNet;

import java.util.List;
import java.util.Set;

public interface FunctionalWeightParser<T extends Number> {
    /**
     *
     * Evaluates the functional expression
     *
     * @param expression to evaluates
     * @return evaluated expression
     */
    public FunctionalResults<T> evaluateExpression(String expression);


}
