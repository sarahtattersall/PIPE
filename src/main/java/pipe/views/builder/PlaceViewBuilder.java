package pipe.views.builder;

import pipe.models.Place;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

import java.util.LinkedList;

public class PlaceViewBuilder {
    Place place;

    public PlaceViewBuilder(Place place) {
        this.place = place;
    }

    public PlaceView build() {
        PlaceView view =
                new PlaceView(place.getId(), place.getName(), new LinkedList<MarkingView>(), place);
        return view;
    }

}
