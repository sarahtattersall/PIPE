package pipe.gui.widgets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.exceptions.IncludeException;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;

@RunWith(MockitoJUnitRunner.class)
public class IncludeHierarchyPanelTest {

	@Mock
	PipeApplicationController controller;
	
	@Mock
	PipeApplicationView view; 
	
	@Mock
	PetriNetController petriNetController;

	private IncludeHierarchy includes;
	private IncludeHierarchyPanel includePanel; 

	@Before
	public void setUp() throws Exception {
		includes = new IncludeHierarchy(new PetriNet(new NormalPetriNetName("p1")), "top"); 
		includes.include(new PetriNet(new NormalPetriNetName("p2")), "2two").
		  include(new PetriNet(new NormalPetriNetName("p3")), "three").
		  include(new PetriNet(new NormalPetriNetName("p4")), "four"); 
		includes.getChildInclude("2two").include(new PetriNet(new NormalPetriNetName("p5")), "five"); 
		includes.include(new PetriNet(new NormalPetriNetName("p6")), "6six"); 
		// one ("top")
		//    two
		//       five
		//       three
		//           four
		//    six 
		when(controller.getActiveIncludeHierarchy()).thenReturn(includes); 
		includePanel = new IncludeHierarchyPanel(controller);
	}

	@Test
	public void displaysInformationForRootIncludeHierarchyByDefault() {
		assertEquals("top",includePanel.nameTextField.getText()); 
	}
	@Test
	public void displaysInformationForActiveIncludeHierarchy() throws IncludeException {
		includePanel.propertyChange(new PropertyChangeEvent(this, 
				PipeApplicationController.NEW_ACTIVE_INCLUDE_HIERARCHY, null, includes.getInclude("2two")));
		assertEquals("2two",includePanel.nameTextField.getText()); 
		assertEquals("2two",includePanel.getTreeEditorPanel().getTree().getSelectionPath().getLastPathComponent().toString());
	}
	
}
