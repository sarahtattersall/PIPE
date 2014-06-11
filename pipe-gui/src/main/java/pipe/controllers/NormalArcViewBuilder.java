package pipe.controllers;

import pipe.actions.gui.PipeApplicationModel;
import pipe.handlers.ArcHandler;
import pipe.views.NormalArcView;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.Container;

/**
 * Builder class to build the view for a normal arc
 */
public class NormalArcViewBuilder {
    /**
     * Underlying arc model
     */
    private final Arc<? extends Connectable, ? extends Connectable> arc;

    /**
     * Petri net controller for the Petri net the arc belongs to
     */
    private final PetriNetController controller;

    /**
     *
     * @param arc underlying arc model
     * @param controller Petri net controller for the Petri net the arc belongs to
     */
    public NormalArcViewBuilder(Arc<? extends Connectable, ? extends Connectable> arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /**
     * Builds an arc view
     *
     * @param parent the parent of this arc
     */
    public NormalArcView<Connectable, Connectable> build(Container parent, PipeApplicationModel model) {

        ArcHandler<? extends Connectable, ? extends Connectable> handler =
                new ArcHandler<>(parent, arc, controller, model);
        NormalArcView<Connectable, Connectable> view =
                new NormalArcView<>((Arc<Connectable, Connectable>) arc, controller, parent, handler, model);
        return view;

    }
}
