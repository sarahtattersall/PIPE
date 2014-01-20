package pipe.visitor.connectable.arc;

import pipe.models.component.Connectable;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

/**
 * A class to determine if an inhibitor arc can be built from the source
 */
public class InhibitorSourceVisitor implements ArcSourceVisitor {

    boolean canCreate = false;

    @Override
    public void visit(Place place) {
        canCreate = true;
    }

    @Override
    public void visit(Transition transition) {
        canCreate = false;
    }

    /**
     * @return the result of the last item visited
     */
    @Override
    public boolean canStart(Connectable connectable) {
        connectable.accept(this);
        return canCreate;
    }
}
