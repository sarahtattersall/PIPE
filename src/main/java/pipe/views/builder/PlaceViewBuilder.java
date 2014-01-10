package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.Place;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

import java.util.LinkedList;

public class PlaceViewBuilder {
    private final Place place;
    private final PetriNetController controller;

    public PlaceViewBuilder(Place place, PetriNetController controller) {
        this.place = place;
        this.controller = controller;
    }

    public PlaceView build() {
        PlaceView view =
                new PlaceView(place.getId(), place.getName(), new LinkedList<MarkingView>(), place, controller);
        return view;
    }

}
