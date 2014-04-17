package pipe.models.petrinet;

import java.util.LinkedList;
import java.util.List;

public class FunctionalEvaluationException extends Exception {
    private final List<String> errors;

    public FunctionalEvaluationException(List<String> errors) {

        this.errors = errors;
    }

    public FunctionalEvaluationException(String message) {
        this.errors = new LinkedList<>();
        errors.add(message);
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (String error : errors) {
            builder.append(error).append("\n");
        }
        return builder.toString();
    }
}
