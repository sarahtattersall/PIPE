package pipe.naming;

import org.junit.Before;
import org.junit.Test;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.place.Place;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlaceNamerTest {
    PetriNet petriNet;

    PlaceNamer placeNamer;

    @Before
    public void setUp() {
        petriNet = new PetriNet();
        placeNamer = new PlaceNamer(petriNet);
    }

    @Test
    public void firstPlaceIsZero() {
        String actual = placeNamer.getName();
        assertEquals("P0", actual);
    }

    @Test
    public void returnP0IfPlaceNotCreated() {
        String first = placeNamer.getName();
        assertEquals("P0", first);
        String second = placeNamer.getName();
        assertEquals("P0", second);
    }

    @Test
    public void returnP0IfPlacesDontConflict() {
        Place place = new Place("P1", "P1");
        petriNet.addPlace(place);
        String actual = placeNamer.getName();
        assertEquals("P0", actual);
    }

    @Test
    public void returnNextValueIfTwoPlacesExist() {
        addNConsecutivePlaces(2);
        String actual = placeNamer.getName();
        assertEquals("P2", actual);
    }


    @Test
    public void returnNextValueIfFourPlacesExist() {
        addNConsecutivePlaces(4);
        String actual = placeNamer.getName();
        assertEquals("P4", actual);
    }

    @Test
    public void returnMiddleValue() {
        Place place = new Place("P0", "P0");
        Place place2 = new Place("P2", "P2");
        petriNet.addPlace(place);
        petriNet.addPlace(place2);

        String actual = placeNamer.getName();
        assertEquals("P1", actual);
    }


    @Test
    public void reUseDeletedValue() {
        Place place = new Place("P0", "P0");
        petriNet.addPlace(place);

        String actual = placeNamer.getName();
        assertEquals("P1", actual);
        petriNet.removePlace(place);

        String actual2 = placeNamer.getName();
        assertEquals("P0", actual2);
    }


    /**
     * Since the PlaceNamer works via listening for change events
     * we need to make sure if Places exist in the petrinet on construction
     * it still is aware of their names.
     */
    @Test
    public void returnCorrectValueAfterConsturctor() {
        Place place = new Place("P0", "P0");
        petriNet.addPlace(place);
        petriNet.addPlace(place);

        PlaceNamer newNamer = new PlaceNamer(petriNet);

        String actual = newNamer.getName();
        assertEquals("P1", actual);
    }


    private void addNConsecutivePlaces(int n) {
        for (int i = 0; i < n; i++) {
            String id = "P" + i;
            Place place = new Place(id, id);
            petriNet.addPlace(place);
        }
    }

    @Test
    public void identifiesNonUniqueName() {
        String name = "Place 0";
        Place place = new Place(name, name);
        petriNet.addPlace(place);
        PlaceNamer newNamer = new PlaceNamer(petriNet);

        assertFalse(newNamer.isUniqueName(name));
    }


    @Test
    public void identifiesUniqueName() {
        String name = "Place 0";
        Place place = new Place(name, name);
        petriNet.addPlace(place);
        PlaceNamer newNamer = new PlaceNamer(petriNet);

        assertTrue(newNamer.isUniqueName("Transition 1"));
    }

    @Test
    public void observesTransitionNameChanges() {
        String originalId = "Place 0";
        Place place = new Place(originalId, originalId);
        petriNet.addPlace(place);
        UniqueNamer newNamer = new PlaceNamer(petriNet);
        String newId = "Place 1";
        place.setId(newId);
        assertFalse(newNamer.isUniqueName(newId));
        assertTrue(newNamer.isUniqueName(originalId));
    }


}
