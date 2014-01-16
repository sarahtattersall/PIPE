package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.component.arc.Arc;
import pipe.models.component.Connectable;
import pipe.views.NormalArcView;

public class NormalArcViewBuilder {
    private final Arc<? extends Connectable, ? extends Connectable> arc;
    private final PetriNetController controller;

    public NormalArcViewBuilder(Arc<? extends Connectable, ? extends Connectable> arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /*
    double startPositionXInput, double startPositionYInput,
    double endPositionXInput, double endPositionYInput,
    ConnectableView sourceInput,
    ConnectableView targetInput, LinkedList<MarkingView> weightInput,
    String idInput, boolean taggedInput, NormalArc model) {     */
    public NormalArcView<Connectable, Connectable> build() {
        NormalArcView<Connectable, Connectable> view =
                new NormalArcView<Connectable, Connectable>((Arc<Connectable,Connectable>) arc, controller);
        return view;

    }
}
