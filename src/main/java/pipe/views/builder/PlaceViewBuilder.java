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
        int capacity = new Double(place.getCapacity()).intValue();
        PlaceView view =
                new PlaceView(place.getX(), place.getY(), place.getId(), place.getName(), place.getNameXOffset(),
                        place.getNameYOffset(), new LinkedList<MarkingView>(), place.getMarkingXOffset(),
                        place.getMarkingYOffset(), capacity);
        view.setModel(place);
        return view;
    }

}
