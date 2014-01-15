package pipe.petrinet.adapters.modelAdapter;

import pipe.models.component.Place;
import pipe.petrinet.adapters.model.AdaptedPlace;
import pipe.petrinet.adapters.model.PositionGraphics;
import pipe.petrinet.adapters.model.Point;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class PlaceAdapter extends XmlAdapter<AdaptedPlace, Place> {
    private final Map<String, Place> places;

    /**
     * Empty contructor needed for marshelling. Since the method to marshell does not actually
     * use these fields it's ok to initialise them as empty/null.
     */
    public PlaceAdapter() {
        places = new HashMap<String, Place>();
    }

    public PlaceAdapter(Map<String, Place> places) {

        this.places = places;
    }

    @Override
    public Place unmarshal(AdaptedPlace adaptedPlace) throws Exception {
        Place place = new Place(adaptedPlace.getId(), adaptedPlace.getName());
        place.setCapacity(adaptedPlace.getCapacity());
        place.setX(adaptedPlace.getGraphics().point.getX());
        place.setY(adaptedPlace.getGraphics().point.getY());
        places.put(place.getId(), place);
        place.setTokenCounts(adaptedPlace.getTokenCounts());
        return place;
    }

    @Override
    public AdaptedPlace marshal(Place place) throws Exception {
        AdaptedPlace adapted = new AdaptedPlace();
        adapted.setId(place.getId());
        adapted.setName(place.getName());
        adapted.setCapacity(place.getCapacity());
        adapted.setTokenCounts(place.getTokenCounts());

        PositionGraphics graphics = new PositionGraphics();
        graphics.point = new Point();
        graphics.point.setX(place.getX());
        graphics.point.setY(place.getY());

        adapted.setGraphics(graphics);

        return adapted;
    }
}
