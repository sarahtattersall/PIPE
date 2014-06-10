package pipe.views.builder;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceViewBuilder;
import pipe.gui.PetriNetTab;
import pipe.actions.gui.PipeApplicationModel;
import pipe.views.PlaceView;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.Place;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PlaceViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Place place;
    PlaceViewBuilder builder;
    @Mock
    PetriNetController mockController;

    @Mock
    private PipeApplicationModel model;
    @Mock
    PetriNetTab parent;

    @Before
    public void setUp()
    {
        place = new DiscretePlace("id", "name");
        builder = new PlaceViewBuilder(place, mockController);
    }

    @Test
    public void correctlySetsModel()
    {
        PlaceView view = builder.build(parent, model);
        assertEquals(place, view.getModel());
    }

    @Test
    public void correctlySetsModelProperties()
    {
        place.setX(200);
        PlaceView view = builder.build(parent, model);
        assertEquals(place.getCapacity(), view.getCapacity());
        assertEquals(place.getMarkingXOffset(), view.getMarkingOffsetXObject(), DOUBLE_DELTA);
        assertEquals(place.getMarkingYOffset(), view.getMarkingOffsetYObject(), DOUBLE_DELTA);
        assertEquals(place.getId(), view.getId());
    }
}
