package pipe.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 18/10/2013
 * Time: 12:16
 * To change this template use File | Settings | File Templates.
 */
public class PlaceTest {

    Place place;

    @Before
    public void setUp()
    {
        place = new Place("test", "test");
    };


    @Test
    public void placeObjectIsSelectable()
    {
       assertTrue(place.isSelectable());
    }
}
