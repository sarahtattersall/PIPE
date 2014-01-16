package pipe.naming;

import pipe.models.component.place.Place;
import pipe.models.petrinet.PetriNet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that attempts to produce names for {@link pipe.models.component.place.Place}
 * that are distinct from others.
 */
public class PlaceNamer implements PetriNetComponentNamer {
    private final PetriNet petriNet;

    private Set<String> placeNames = new HashSet<String>();

    public PlaceNamer(PetriNet petriNet) {

        this.petriNet = petriNet;
        observeChanges(petriNet);
        initialisePlaceNames();
    }

    private void initialisePlaceNames() {
        for (Place place : petriNet.getPlaces()) {
            placeNames.add(place.getId());
        }
    }

    private void observeChanges(PetriNet petriNet) {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals("newPlace")) {
                    Place place = (Place) propertyChangeEvent.getNewValue();
                    placeNames.add(place.getId());
                } else if (name.equals("deletePlace")) {
                    Place place = (Place) propertyChangeEvent.getOldValue();
                    placeNames.remove(place.getId());
                }
            }
        };
        petriNet.addPropertyChangeListener(listener);
    }

    @Override
    public String getName() {
        int placeNumber = 0;
        String name = "P" + placeNumber;
        while (placeNames.contains(name)) {
            placeNumber++;
            name = "P" + placeNumber;
        }
        return name;
    }
}
