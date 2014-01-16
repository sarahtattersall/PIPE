package pipe.visitor;

import pipe.models.component.Place;

public interface PlaceVisitor extends PetriNetComponentVisitor {
    public void visit(Place place);
}
