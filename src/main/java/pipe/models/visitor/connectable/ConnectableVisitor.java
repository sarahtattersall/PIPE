package pipe.models.visitor.connectable;

import pipe.models.component.ConditionalPlace;
import pipe.models.component.Place;
import pipe.models.component.TemporaryArcTarget;
import pipe.models.component.Transition;

public interface ConnectableVisitor {
    public void visit(Place place);
    public void visit(Transition transition);
    public void visit(TemporaryArcTarget arcTarget);
    void visit(ConditionalPlace conditionalPlace);
}
