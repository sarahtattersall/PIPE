package pipe.visitor.connectable;

import pipe.models.component.ConditionalPlace;
import pipe.models.component.Place;
import pipe.models.component.Transition;

public interface ConnectableVisitor {
    public void visit(Place place);

    public void visit(Transition transition);

    void visit(ConditionalPlace conditionalPlace);
}
