package pipe.models.visitor.connectable.arc;

import pipe.models.component.*;

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

    @Override
    public void visit(final TemporaryArcTarget arcTarget) {
        canCreate = false;
    }

    @Override
    public void visit(final ConditionalPlace conditionalPlace) {
        canCreate = false;
    }

    /**
     * @return the result of the last item visited
     */
    @Override
    public boolean canCreate(Connectable connectable) {
        connectable.accept(this);
        return canCreate;
    }

}