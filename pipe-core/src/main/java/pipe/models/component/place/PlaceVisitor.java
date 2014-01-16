package pipe.models.component.place;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface PlaceVisitor extends PetriNetComponentVisitor {
    public void visit(Place place);
}
