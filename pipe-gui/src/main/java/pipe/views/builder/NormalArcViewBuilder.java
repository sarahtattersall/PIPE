package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.views.arc.NormalArcView;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Arc;

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
                new NormalArcView<>((Arc<Connectable,Connectable>) arc, controller);
        return view;

    }
}
