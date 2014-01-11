package pipe.utilities.math;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Place;
import pipe.models.component.Transition;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class IncidenceMatrixTest {

    private IncidenceMatrix incidenceMatrix;

    @Before
    public void setUp(){
        incidenceMatrix = new IncidenceMatrix();
    }

    @Test
    public void voidEntryReturnsZero() {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        int value = incidenceMatrix.get(place, transition);
        assertEquals("Does not behave like matrix for values not put in matrix", 0, value);
    }


    @Test
    public void putEntryReturnsValue() {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        int expected = 4;
        incidenceMatrix.put(place, transition, expected);
        int actual = incidenceMatrix.get(place, transition);
        assertEquals("Does not behave like matrix for values not put in matrix", expected, actual);
    }

    @Test
    public void putEntryOverwritesValue() {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        int firstValue = 4;
        incidenceMatrix.put(place, transition, firstValue);
        int secondValue = 5;
        incidenceMatrix.put(place, transition, secondValue);
        int actual = incidenceMatrix.get(place, transition);
        assertEquals("Did not override values correctly", secondValue, actual);
    }

}
