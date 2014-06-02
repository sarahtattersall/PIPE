package pipe.controllers;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.PlaceHandler;
import pipe.views.PlaceView;
import uk.ac.imperial.pipe.models.petrinet.Place;

import java.awt.Container;

public class PlaceViewBuilder {
    private final Place place;
    private final PetriNetController controller;

    public PlaceViewBuilder(Place place, PetriNetController controller) {
        this.place = place;
        this.controller = controller;
    }

    public PlaceView build(Container parent, PipeApplicationModel model) {
        PlaceHandler handler = new PlaceHandler(parent, place, controller, model);
        return new PlaceView(place, parent, controller, handler);
    }

}
