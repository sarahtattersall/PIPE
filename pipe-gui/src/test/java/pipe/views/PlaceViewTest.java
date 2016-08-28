package pipe.views;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceViewBuilder;
import pipe.gui.PetriNetTab;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.Place;

@RunWith(MockitoJUnitRunner.class)
public class PlaceViewTest {

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
	public void repaintCalledWhenUnderlyingPlaceChanges() throws Exception {
    	PlaceView view = builder.build(parent, model);
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        view.addListenerToModel(mockListener); 
        view.addListenerToModel(view); 
        place.setTokenCount("Default", 7);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
        assertTrue(view.repaintedForTesting()); 
	}

}
