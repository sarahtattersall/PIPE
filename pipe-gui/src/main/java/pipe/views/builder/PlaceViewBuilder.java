package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.views.PlaceView;
import uk.ac.imperial.pipe.models.component.place.Place;

public class PlaceViewBuilder {
    private final Place place;
    private final PetriNetController controller;

    public PlaceViewBuilder(Place place, PetriNetController controller) {
        this.place = place;
        this.controller = controller;
    }

    public PlaceView build() {
        return new PlaceView(place, controller);
    }

}
