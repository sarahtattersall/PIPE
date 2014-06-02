package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.ArcHandler;
import pipe.views.arc.InhibitorArcView;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.awt.Container;

public class InhibitorArcViewBuilder {
    private final Arc<Place, Transition> arc;
    private final PetriNetController controller;

    public InhibitorArcViewBuilder(Arc<Place, Transition> arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /**
     * @param parent, the parent of this arc view
     * @return an inhibitor arc view
     */
    public InhibitorArcView build(Container parent, PipeApplicationModel model) {
        ArcHandler<? extends Connectable, ? extends Connectable> handler = new ArcHandler<>(parent, arc, controller, model);
        InhibitorArcView view =
                new InhibitorArcView(arc, controller, parent, handler, model);
        return view;

    }
}
