package pipe.views;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeEvent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.PipeApplicationModel;
import pipe.actions.gui.ZoomUI;
import pipe.controllers.application.PipeApplicationController;
import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;

@RunWith(MockitoJUnitRunner.class)
public class PipeApplicationViewTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


	
	private PipeApplicationView view;
	private PipeApplicationModel model;
	private PipeApplicationController controller;
	private ZoomUI zoomUI;



	private PetriNetChangeListener listener;

	@SuppressWarnings("serial")
	@Before
	public void setUp() throws Exception {
		model = new PipeApplicationModel(""); 
		controller = new PipeApplicationController(model);
		zoomUI = new ZoomUI(1, 0.1, 3, 0.4, controller);
		view = new PipeApplicationView(zoomUI, controller, model) {
			@Override
			protected void buildView(
					PipeApplicationController applicationController,
					PipeApplicationModel applicationModel) {
			}
			@Override
			public void updateZoomCombo() {
			}
			@Override
			public void refreshTokenClassChoices() {
			}
		};
	}
	
	@Test
	public void verifyPetriNetNameIsUsedForTabTitleIfEventIsPetriNet() {
		PetriNet petriNet = new PetriNet(new NormalPetriNetName("petriNet1")); 
		listener = new PetriNetChangeListener(view);
		listener.propertyChange(new PropertyChangeEvent(this, 
				PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE, null, petriNet));
		assertEquals("petriNet1", view.frameForPetriNetTabs.getTitleAt(0)); 
//        verify(listener).propertyChange(argThat(PropertyChangeUtils.hasName(PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE)));		
	}
	@Test
	public void verifyIncludeNameIsUsedForTabTitleIfEventIsIncludeHierarchy() {
		PetriNet petriNet = new PetriNet(new NormalPetriNetName("petriNet1"));
		IncludeHierarchy include = new IncludeHierarchy(petriNet, "includeA");
		listener = new PetriNetChangeListener(view);
		listener.propertyChange(new PropertyChangeEvent(this, 
				PetriNetManagerImpl.NEW_INCLUDE_HIERARCHY_MESSAGE, null, include));
		assertEquals("includeA", view.frameForPetriNetTabs.getTitleAt(0)); 
		assertEquals(1, view.frameForPetriNetTabs.getTabCount()); 
	}
	//TODO do we need this test?
//	@Test
	public void verifyIncludeNameIsRemovedWhenTabRemoved() {
		PetriNet petriNet = new PetriNet(new NormalPetriNetName("petriNet1"));
		IncludeHierarchy include = new IncludeHierarchy(petriNet, "includeA");
		listener = new PetriNetChangeListener(view);
		listener.propertyChange(new PropertyChangeEvent(this, 
				PetriNetManagerImpl.NEW_INCLUDE_HIERARCHY_MESSAGE, null, include));
		assertEquals(1, view.frameForPetriNetTabs.getTabCount()); 
		listener.propertyChange(new PropertyChangeEvent(this, 
				PetriNetManagerImpl.REMOVE_INCLUDE_HIERARCHY_MESSAGE, null, include));
		assertEquals(0, view.frameForPetriNetTabs.getTabCount()); 
	}

}
