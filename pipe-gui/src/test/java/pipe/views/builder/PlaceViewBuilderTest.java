package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.models.component.place.Place;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PlaceView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PlaceViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Place place;
    PlaceViewBuilder builder;
    PetriNetController mockController;

    @Before
    public void setUp()
    {
        mockController = mock(PetriNetController.class);
        place = new Place("id", "name");
        builder = new PlaceViewBuilder(place, mockController);
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
        place.setX(200);
        PlaceView view = builder.build();
        assertEquals(place.getCapacity(), view.getCapacity());
        assertEquals(place.getMarkingXOffset(), view.getMarkingOffsetXObject(), DOUBLE_DELTA);
        assertEquals(place.getMarkingYOffset(), view.getMarkingOffsetYObject(), DOUBLE_DELTA);
        assertEquals(place.getName(), view.getName());
        assertEquals(place.getId(), view.getId());
    }
}
