package pipe.views;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.xml.transform.TransformerFactoryConfigurationError;

import matchers.component.HasModel;
import org.junit.Before;
import org.junit.Test;

import pipe.controllers.PetriNetController;
import pipe.gui.ZoomController;
import pipe.models.PetriNet;
import pipe.models.component.Place;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.utilities.transformers.PNMLTransformerTest;

import java.util.Observer;

public class PetriNetViewTest
{

	private PetriNetView petriNetView;
	private PlaceView placeView;
	private MarkingView markingView;

	@Before
	public void setUp() throws Exception
	{
		petriNetView = new PetriNetView(null, new PetriNet());
	}
	@Test
	public void verifyAnimationModePossibleForNewPetriNewViewWhenNetFileHasToken() throws Exception
	{
		checkAnimationModePossibleForNewPetriNet(PNMLTransformerTest.SIMPLE_NET_WITH_TOKEN); 
	}
	@Test
	public void verifyAnimationModePossibleForNewPetriNetViewWhenNetFileDoesNotHaveToken() throws Exception
	{
		checkAnimationModePossibleForNewPetriNet(PNMLTransformerTest.SIMPLE_NET_WITHOUT_TOKEN); 
	}
	protected void checkAnimationModePossibleForNewPetriNet(String net)
			throws TransformerFactoryConfigurationError
	{
//		assertNotNull("not null at creation",petriNetView.getTokenViews());
//		buildPetriNetViewFromXmlString(net);
//		assertNotNull("emptied, but token set controller re-created",petriNetView.getTokenViews());
//		assertNull("no matrices created yet",petriNetView.getTokenViews().get(0).getForwardsIncidenceMatrix());
//		petriNetView.setEnabledTransitions();
//		assertNotNull("matrices should be created now",petriNetView.getTokenViews().get(0).getForwardsIncidenceMatrix());
	}
	protected void buildPetriNetViewFromXmlString(String net)
			throws TransformerFactoryConfigurationError
	{
		PNMLTransformer transformer = new PNMLTransformer(); 
		petriNetView.createFromPNML(transformer.transformPNMLStreamSource(PNMLTransformerTest
				.getNetAsStreamSource(net)));
	}

	@Test
	public void verifyDefaultTokenViewCreatedDuringConstruction() throws Exception
	{
		assertEquals("Default created",1, petriNetView.getTokenViews().size());
		assertEquals("Default", petriNetView.getTokenViews().get(0).getID());
	}

    @Test
    public void displaysPlaceOnTab()
    {
        Place place = new Place("id", "name");
        PetriNet net = new PetriNet();
        net.addPlace(place);

        PetriNetController controller = mock(PetriNetController.class);
        ZoomController zoomController = mock(ZoomController.class);
        when(controller.getZoomController()).thenReturn(zoomController);

        PetriNetView view = new PetriNetView(controller, net);
        Observer mockObserver = mock(Observer.class);
        view.addObserver(mockObserver);

        view.update();
        verify(mockObserver).update(eq(view), argThat(new HasModel<Place, PlaceView>(place)));
    }
}
