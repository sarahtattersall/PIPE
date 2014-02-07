package pipe.models.component.place;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface PlaceVisitor extends PetriNetComponentVisitor {
    void visit(Place place);
}
