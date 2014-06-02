package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.ArcHandler;
import pipe.views.NormalArcView;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.Container;

public class NormalArcViewBuilder {
    private final Arc<? extends Connectable, ? extends Connectable> arc;
    private final PetriNetController controller;

    public NormalArcViewBuilder(Arc<? extends Connectable, ? extends Connectable> arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /**
     *
     * Builds an arc view
     * @param parent the parent of this arc
     */
    public NormalArcView<Connectable, Connectable> build(Container parent, PipeApplicationModel model) {

        ArcHandler<? extends Connectable, ? extends Connectable> handler = new ArcHandler<>(parent, arc, controller, model);
        NormalArcView<Connectable, Connectable> view =
                new NormalArcView<>((Arc<Connectable,Connectable>) arc, controller, parent, handler, model);
        return view;

    }
}
