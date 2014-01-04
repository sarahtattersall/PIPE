package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.component.Arc;
import pipe.views.InhibitorArcView;
import pipe.views.MarkingView;

import java.util.LinkedList;

public class InhibitorArcViewBuilder {
    private final Arc arc;
    private final PetriNetController controller;

    public InhibitorArcViewBuilder(Arc arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /*
    double startPositionXInput, double startPositionYInput,
    double endPositionXInput, double endPositionYInput,
    ConnectableView sourceInput,
    ConnectableView targetInput, LinkedList<MarkingView> weightInput,
    String idInput, boolean taggedInput, NormalArc model) {     */
    public InhibitorArcView build() {
        InhibitorArcView view =
                new InhibitorArcView(arc, controller);
        return view;

    }
}
