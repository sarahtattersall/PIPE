package pipe.parsers;

import pipe.models.petrinet.PetriNet;

import java.util.List;
import java.util.Set;

public interface FunctionalWeightParser<T extends Number> {
    /**
     * @return true if parsing threw errors
     */
    public boolean containsErrors();

    /**
     * @return errors generated from parsing expression
     */
    public List<String> getErrors();

    /**
     *
     * Evaluates the expression against no Petri net
     * If the expression contains references to the Petri net then
     * this will throw an error
     *
     * @return evaluated expression
     */
    public T evaluateExpression() throws UnparsableException;


}
