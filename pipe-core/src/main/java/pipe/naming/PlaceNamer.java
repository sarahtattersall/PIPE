package pipe.naming;

import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
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

    private final Set<String> placeNames = new HashSet<String>();

    private final PropertyChangeListener nameChangeListener = new NameChangeListener(placeNames);

    public PlaceNamer(PetriNet petriNet) {

        this.petriNet = petriNet;
        observeChanges(petriNet);
        initialisePlaceNames();
    }

    private void initialisePlaceNames() {
        for (Place place : petriNet.getPlaces()) {
            place.addPropertyChangeListener(nameChangeListener);
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
                    place.addPropertyChangeListener(nameChangeListener);
                    placeNames.add(place.getId());
                } else if (name.equals("deletePlace")) {
                    Place place = (Place) propertyChangeEvent.getOldValue();
                    place.removePropertyChangeListener(nameChangeListener);
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

    @Override
    public boolean isUniqueName(String name) {
        return !placeNames.contains(name);
    }
}
