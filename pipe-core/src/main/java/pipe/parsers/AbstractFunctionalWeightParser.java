package pipe.parsers;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class, implements some common functionality of weight parsers
 */
public abstract class AbstractFunctionalWeightParser<T extends Number> implements FunctionalWeightParser<T> {

    AbstractFunctionalWeightParser(String expression) {
        parseTree = GrammarUtils.parse(expression);
    }

    /**
     * Parsed expression
     */
    protected final ParseTree parseTree;

    /**
     * Errors occurred whilst parsing
     */
    protected final List<String> errors = new LinkedList<>();


    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public boolean containsErrors() {
        return !errors.isEmpty();
    }

    /**
     *
     * @param evalVisitor
     * @return
     * @throws UnparsableException
     */
    protected Double getValue(ParseTreeVisitor<Double> evalVisitor) throws UnparsableException {
        if (containsErrors()) {
            throw new UnparsableException("There were errors in parsing the expression, cannot calculate value!");
        } else {
            return evalVisitor.visit(parseTree);
        }
    }
}
