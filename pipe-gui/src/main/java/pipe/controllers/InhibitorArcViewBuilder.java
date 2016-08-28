package pipe.controllers;

import pipe.actions.gui.PipeApplicationModel;
import pipe.handlers.ArcHandler;
import pipe.views.InhibitorArcView;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.awt.Container;

/**
 * Builds the view representation of an inhibitor arc
 */
public class InhibitorArcViewBuilder {
    /**
     * Underlying arc model
     */
    private final Arc<Place, Transition> arc;

    /**
     * Controller for the Petri net the arc belongs to
     */
    private final PetriNetController controller;

    /**
     *
     * @param arc underlying arc model
     * @param controller controller for the Petri net the arc belongs to
     */
    public InhibitorArcViewBuilder(Arc<Place, Transition> arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /**
     * @param parent, the parent of this arc view
     * @param model application model 
     * @return an inhibitor arc view
     */
    public InhibitorArcView build(Container parent, PipeApplicationModel model) {
        ArcHandler<? extends Connectable, ? extends Connectable> handler = new ArcHandler<>(parent, arc, controller, model);
        InhibitorArcView view =
                new InhibitorArcView(arc, controller, parent, handler, model);
        return view;

    }
}
