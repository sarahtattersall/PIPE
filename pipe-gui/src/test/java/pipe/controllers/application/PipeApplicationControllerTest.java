package pipe.controllers.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observer;

import javax.swing.event.UndoableEditListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.CopyPasteManager;
import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.ZoomController;
import pipe.gui.PetriNetTab;
import uk.ac.imperial.pipe.exceptions.IncludeException;
import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;

@RunWith(MockitoJUnitRunner.class)
public class PipeApplicationControllerTest {
    PipeApplicationController applicationController;

    @Mock
    private PipeApplicationModel model;

    @Mock
    private PropertyChangeListener listener;    
    
	private IncludeHierarchy include;

    @Before
    public void setUp() throws IncludeException {
        applicationController = new PipeApplicationController(model) {
        	@Override
        	protected GUIAnimator buildAnimatorWithHistory(PetriNet net,
        			Observer historyObserver) {
        		return null; 
        	}
        	@Override
        	protected ZoomController buildZoomController(PetriNetTab tab) {
        		return null; 
        	}
        	@Override
        	protected CopyPasteManager buildCopyPasteManager(PetriNet net,
        			PetriNetTab tab, UndoableEditListener undoListener) {
        		return null;
        	}
        	@Override
        	protected void initialiseNet(PetriNet net,
        			PropertyChangeListener propertyChangeListener) {
        	}
        };
        include = new IncludeHierarchy(new PetriNet(new NormalPetriNetName("net1")), "a");
        include.include(new PetriNet(new NormalPetriNetName("net2")), "b"); 
        include.include(new PetriNet(new NormalPetriNetName("net3")), "c"); 
   }
    @Test
	public void verifyControllerListensForNewRootLevelIncludeHierarchy() throws Exception {
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include));
    	assertEquals(include, applicationController.getActiveIncludeHierarchy());
	}
    @Test
    public void verifyControllerExpectsToProcessNewIncludeMessagesForRootAndEachChild() throws Exception {
    	assertFalse(applicationController.areIncludeAdditionsPending()); 
    	assertEquals(0,applicationController.expectedIncludeCount); 
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include));
    	assertTrue(applicationController.areIncludeAdditionsPending()); 
    	assertEquals(3,applicationController.expectedIncludeCount); 
        applicationController.registerTab(include.getPetriNet(), new PetriNetTab(), null, null, null); 
        assertTrue(applicationController.areIncludeAdditionsPending()); 
        applicationController.registerTab(include.getInclude("b").getPetriNet(), new PetriNetTab(), null, null, null); 
        assertTrue(applicationController.areIncludeAdditionsPending()); 
        applicationController.registerTab(include.getInclude("c").getPetriNet(), new PetriNetTab(), null, null, null); 
        assertFalse(applicationController.areIncludeAdditionsPending()); 
        assertEquals(0,applicationController.expectedIncludeCount); 
    }
    @Test
    public void verifyControllerDecrementsCounterOnlyForIncludeHierarchies() throws Exception {
    	assertFalse(applicationController.areIncludeAdditionsPending()); 
    	assertEquals(0,applicationController.expectedIncludeCount);
    	PetriNet net = new PetriNet(); 
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE, null, net));
    	applicationController.registerTab(net, new PetriNetTab(), null, null, null); 
    	assertFalse(applicationController.areIncludeAdditionsPending()); 
    	assertEquals(0,applicationController.expectedIncludeCount); 
    }

    @SuppressWarnings("unused")
	@Test
    public void verifyControllerReturnedForCorrespondingPetriNetForEachRegisteredTabOfIncludeHierarchy() throws Exception {
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include));
    	PetriNet expectedPetriNet = include.getPetriNet(); 
    	PetriNetTab tabA = new PetriNetTab(); 
    	PetriNetController controller = 
    			applicationController.registerTab(expectedPetriNet, tabA, null, null, null);
    	assertEquals(expectedPetriNet, applicationController.getPetriNetController(tabA).getPetriNet()); 

    	PetriNetTab tabB = new PetriNetTab(); 
    	expectedPetriNet = include.getInclude("b").getPetriNet(); 
    	controller = applicationController.registerTab(expectedPetriNet, tabB, null, null, null); 
    	assertEquals(expectedPetriNet, applicationController.getPetriNetController(tabB).getPetriNet()); 

    	PetriNetTab tabC = new PetriNetTab(); 
    	expectedPetriNet = include.getInclude("c").getPetriNet(); 
    	controller = applicationController.registerTab(expectedPetriNet, tabC, null, null, null); 
    	assertEquals(expectedPetriNet, applicationController.getPetriNetController(tabC).getPetriNet()); 
    }
    @Test
    public void verifyTabAssociatedWithEachLevelOfIncludeHierarchy() throws Exception {
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include));
    	PetriNet expectedPetriNet = include.getPetriNet(); 
    	PetriNetTab tabA = new PetriNetTab(); 
    	applicationController.registerTab(include.getPetriNet(), tabA, null, null, null);
    	assertEquals(tabA, applicationController.getTab(include)); 
    	
    	PetriNetTab tabB = new PetriNetTab(); 
    	applicationController.registerTab(include.getInclude("b").getPetriNet(), tabB, null, null, null); 
    	assertEquals(tabB, applicationController.getTab(include.getInclude("b"))); 
    	
    	PetriNetTab tabC = new PetriNetTab(); 
    	applicationController.registerTab(include.getInclude("c").getPetriNet(), tabC, null, null, null); 
    	assertEquals(tabC, applicationController.getTab(include.getInclude("c"))); 
    }
    @Test
    public void verifyActiveTabAndActiveControllerAreThatOfRootLevelOfIncludeHierarchy() throws Exception {
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include));
    	PetriNetTab rootTab = new PetriNetTab(); 
    	PetriNetController controller = applicationController.registerTab(include.getPetriNet(), rootTab, null, null, null);
    	assertEquals(rootTab, applicationController.getActiveTab()); 
    	assertEquals(controller, applicationController.getActivePetriNetController()); 

    	applicationController.registerTab(include.getInclude("b").getPetriNet(), new PetriNetTab(), null, null, null); 
    	assertEquals("active tab unchanged",rootTab, applicationController.getActiveTab()); 
    	assertEquals("active controller unchanged",controller, applicationController.getActivePetriNetController()); 

    	applicationController.registerTab(include.getInclude("c").getPetriNet(), new PetriNetTab(), null, null, null); 
    	assertEquals("active tab still unchanged",rootTab, applicationController.getActiveTab()); 
    	assertEquals("active controller still unchanged",controller, applicationController.getActivePetriNetController()); 
    }
    @Test
	public void ifMultipleRootHierarchiesAreLoadedEachDrivesTheActiveTabAndControllerInTurn() throws Exception {
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include));
    	PetriNetTab rootTab = new PetriNetTab(); 
    	PetriNetController controller = applicationController.registerTab(include.getPetriNet(), rootTab, null, null, null);
    	applicationController.registerTab(include.getInclude("b").getPetriNet(), new PetriNetTab(), null, null, null); 
    	applicationController.registerTab(include.getInclude("c").getPetriNet(), new PetriNetTab(), null, null, null); 
    	assertEquals(rootTab, applicationController.getActiveTab()); 
    	assertEquals(controller, applicationController.getActivePetriNetController()); 

        IncludeHierarchy include2 = new IncludeHierarchy(new PetriNet(new NormalPetriNetName("net4")), "z");
    	applicationController.propertyChange(new PropertyChangeEvent(this, 
    			PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE, null, include2));
    	PetriNetTab rootTab2 = new PetriNetTab(); 
    	PetriNetController controller2 = applicationController.registerTab(include2.getPetriNet(), rootTab2, null, null, null);
    	assertEquals(rootTab2, applicationController.getActiveTab()); 
    	assertEquals(controller2, applicationController.getActivePetriNetController()); 
	}
    @Test
	public void setActiveIncludeForViewNotifiesListeners() throws Exception {
    	applicationController.addPropertyChangeListener(listener); 
    	applicationController.setActiveIncludeHierarchyAndNotifyView(include); 
    	verify(listener, times(1)).propertyChange(any(PropertyChangeEvent.class));
	}
    @Test
    public void setActiveIncludeForTreePanelNotifiesListeners() throws Exception {
    	applicationController.addPropertyChangeListener(listener); 
    	applicationController.setActiveIncludeHierarchyAndNotifyTreePanel(include); 
    	verify(listener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }
}
