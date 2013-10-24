package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.models.Place;
import pipe.views.PlaceView;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class PlaceViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Place place;
    PlaceViewBuilder builder;

    @Before
    public void setUp()
    {
        place = new Place("id", "name");
        builder = new PlaceViewBuilder(place);
    }

    @Test
    public void correctlySetsModel()
    {
        PlaceView view = builder.build();
        assertEquals(place, view.getModel());
    }

    @Test
    public void correctlySetsModelProperties()
    {
        PlaceView view = builder.build();
        assertEquals(place.getCapacity(), view.getCapacity(), DOUBLE_DELTA);
        assertEquals(place.getMarkingXOffset(), view.getMarkingOffsetXObject(), DOUBLE_DELTA);
        assertEquals(place.getMarkingYOffset(), view.getMarkingOffsetYObject(), DOUBLE_DELTA);
        assertEquals(place.getName(), view.getName());
        assertEquals(place.getId(), view.getId());
        assertEquals(place.getX(), view._positionX, DOUBLE_DELTA);
        assertEquals(place.getY(), view._positionY, DOUBLE_DELTA);
        assertEquals(place.getNameXOffset(), view._nameOffsetX, DOUBLE_DELTA);
        assertEquals(place.getNameYOffset(), view._nameOffsetY, DOUBLE_DELTA);

    }
}
