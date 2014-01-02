package pipe.models.visitor.connectable.arc;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.component.*;

public class InhibitorCreatorVisitor implements ArcCreatorVisitor {

    private final PipeApplicationController controller;

    public InhibitorCreatorVisitor(PipeApplicationController controller) {
        this.controller = controller;
    }

    @Override
    public void visit(final Place place) {
        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();
        netController.startCreatingInhibitorArc(place, token);
    }

    @Override
    public void visit(final Transition transition) {
        throw new RuntimeException("Cannot create inhibitor arc from a transition!");
    }

    @Override
    public void visit(final TemporaryArcTarget arcTarget) {
        throw new RuntimeException("Cannot create inhibitor arc from a temporary arc target!");
    }

    @Override
    public void visit(final ConditionalPlace conditionalPlace) {
        throw new RuntimeException("Cannot create inhibitor arc from a conditional place!");
    }
}
