package pipe.visitor.connectable.arc;

import pipe.models.component.Connectable;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

public class NormalArcSourceVisitor implements ArcSourceVisitor {

    boolean canCreate = false;

    @Override
    public void visit(final Place place) {
        canCreate = true;
    }

    @Override
    public void visit(final Transition transition) {
        canCreate = true;
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