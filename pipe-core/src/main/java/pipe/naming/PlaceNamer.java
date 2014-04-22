package pipe.naming;

import pipe.models.component.place.Place;
import pipe.models.petrinet.PetriNet;

/**
 * A class that attempts to produce names for {@link pipe.models.component.place.Place}
 * that are distinct from others.
 */
public class PlaceNamer extends ComponentNamer {

    public PlaceNamer(PetriNet petriNet) {
        super(petriNet, "P", PetriNet.NEW_PLACE_CHANGE_MESSAGE, PetriNet.DELETE_PLACE_CHANGE_MESSAGE);
        initialisePlaceNames();
    }

    private void initialisePlaceNames() {
        for (Place place : petriNet.getPlaces()) {
            place.addPropertyChangeListener(nameListener);
            names.add(place.getId());
        }
    }
}
