package pipe.models.component.place;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface PlaceVisitor extends PetriNetComponentVisitor {
    void visit(Place place);
}
