package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.component.InhibitorArc;
import pipe.views.InhibitorArcView;
import pipe.views.MarkingView;

import java.util.LinkedList;

public class InhibitorArcViewBuilder {
    private final InhibitorArc arc;
    private final PetriNetController controller;

    public InhibitorArcViewBuilder(InhibitorArc arc, PetriNetController controller) {
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
        double startX = arc.getSource().getX();
        double startY = arc.getSource().getY();
        double endX = arc.getTarget().getX();
        double endY = arc.getTarget().getY();
        InhibitorArcView view =
                new InhibitorArcView(startX, startY, endX, endY, null, null, new LinkedList<MarkingView>(), arc.getId(),
                        arc, controller);
        return view;

    }
}
