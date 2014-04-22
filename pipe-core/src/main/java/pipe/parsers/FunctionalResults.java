package pipe.parsers;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FunctionalResults<T extends Number> {
    /**
     * Errors that occured whilst evaluating the expression
     */
    private List<String> errors;

    /**
     * Components referenced by the expression used to create this result
     */
    private final Set<String> components;

    /**
     * Result of evaluating an expression
     * Not valid if contains errors
     */
    private T result;

    public FunctionalResults(T result, List<String> errors, Set<String> components) {

        this.result = result;
        this.errors = errors;
        this.components = components;
    }

    public FunctionalResults(T result, Set<String> components) {
        this(result, new LinkedList<String>(), components);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public T getResult() {
        return result;
    }

    public Set<String> getComponents() {
        return components;
    }
}
