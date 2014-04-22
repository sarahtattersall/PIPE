package pipe.models.component.rate;

import pipe.exceptions.InvalidRateException;
import pipe.visitor.component.PetriNetComponentVisitor;

public interface RateParameterVisitor extends PetriNetComponentVisitor {
    void visit(RateParameter rate) throws InvalidRateException;
}
