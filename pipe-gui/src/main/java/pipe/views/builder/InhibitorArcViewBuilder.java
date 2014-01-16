package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.views.InhibitorArcView;

public class InhibitorArcViewBuilder {
    private final Arc<Place, Transition> arc;
    private final PetriNetController controller;

    public InhibitorArcViewBuilder(Arc<Place, Transition> arc, PetriNetController controller) {
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
