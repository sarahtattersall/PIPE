package pipe.controllers;

import pipe.actions.gui.PipeApplicationModel;
import pipe.handlers.PlaceHandler;
import pipe.views.PlaceView;
import uk.ac.imperial.pipe.models.petrinet.Place;

import java.awt.Container;

/**
 * Builder to create Place Views
 */
public class PlaceViewBuilder {
    /**
     * Underlying plac model
     */
    private final Place place;

    /**
     * Petri net controller for the Petri net that the place is housed in
     */
    private final PetriNetController controller;

    /**
     *
     * @param place Underlying plac model
     * @param controller Petri net controller for the Petri net that the place is housed in
     */
    public PlaceViewBuilder(Place place, PetriNetController controller) {
        this.place = place;
        this.controller = controller;
    }

    /**
     *
     * @param parent parent of the view
     * @param model main PIPE application model
     * @return place view
     */
    public PlaceView build(Container parent, PipeApplicationModel model) {
        PlaceHandler handler = new PlaceHandler(parent, place, controller, model);
        return new PlaceView(place, parent, controller, handler);
    }

}
