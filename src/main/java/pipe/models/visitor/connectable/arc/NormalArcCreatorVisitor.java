package pipe.models.visitor.connectable.arc;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.component.*;

public class NormalArcCreatorVisitor implements ArcCreatorVisitor {

    private final PipeApplicationController controller;

    public NormalArcCreatorVisitor(PipeApplicationController controller) {
        this.controller = controller;
    }

    @Override
    public void visit(final Place place) {
        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();
        netController.startCreatingNormalArc(place, token);
    }

    @Override
    public void visit(final Transition transition) {
        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();
        netController.startCreatingNormalArc(transition, token);
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
