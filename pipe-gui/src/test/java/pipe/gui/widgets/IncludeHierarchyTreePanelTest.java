package pipe.gui.widgets;

import static org.junit.Assert.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import uk.ac.imperial.pipe.exceptions.IncludeException;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;

@RunWith(MockitoJUnitRunner.class)
public class IncludeHierarchyTreePanelTest {

	@Mock
	PipeApplicationController controller;
	
	@Mock
	PetriNetController petriNetController;

	
	private IncludeHierarchy includes;
	private IncludeHierarchyTreePanel treePanel; 
	
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
	}
	@Test
	public void buildsTreeFollowingIncludeHierarchyStructure() {
		treePanel = new IncludeHierarchyTreePanel(controller); 
		assertEquals(6,treePanel.getNodeMap().size()); 
		DefaultMutableTreeNode node = treePanel.getRootNode(); 
		assertEquals("top", node.toString()); 
		assertEquals("2two", node.getChildAt(0).toString()); 
		assertEquals("five", node.getChildAt(0).getChildAt(0).toString()); 
		assertEquals("three", node.getChildAt(0).getChildAt(1).toString()); 
		assertEquals("four", node.getChildAt(0).getChildAt(1).getChildAt(0).toString()); 
		assertEquals("6six", node.getChildAt(1).toString()); 
	}
	@Test
	public void ifNoIncludeHierarchyShowsDefaultFromCurrentPetriNet() throws Exception {
		when(controller.getActiveIncludeHierarchy()).thenReturn(null); 
		when(petriNetController.getPetriNet()).thenReturn(new PetriNet(new NormalPetriNetName("net1"))); 
		when(controller.getActivePetriNetController()).thenReturn(petriNetController); 
		treePanel = new IncludeHierarchyTreePanel(controller); 
		assertEquals("", treePanel.getRootNode().toString()); 
	}
	@Test
	public void ifNoWindowsAreOpenNoNodesAreCreatedForTreePanel() throws Exception {
		// ...which, happily, doesn't seem to care
		when(controller.getActiveIncludeHierarchy()).thenReturn(null); 
		when(controller.getActivePetriNetController()).thenReturn(null); 
		treePanel = new IncludeHierarchyTreePanel(controller);
		assertFalse(treePanel.createNodes()); 
	}
	@Test
	public void isRefreshedFollowingIncludeHierarchyChange() throws IncludeException {
		treePanel = new IncludeHierarchyTreePanel(controller); 
		assertEquals(6,treePanel.getNodeMap().size()); 
		assertEquals("top", treePanel.getRootNode().toString()); 
		includes.include(new PetriNet(new NormalPetriNetName("net7")), "seven");  
		assertEquals("seven", treePanel.getRootNode().getChildAt(2).toString());  
	}
	@Test
	public void nodeCanBeSelectedForDisplay() throws Exception {
		treePanel = new IncludeHierarchyTreePanel(controller); 
		treePanel.select(includes.getInclude("2two").getInclude("five"));
		assertEquals("five",treePanel.getTree().getSelectionPath().getLastPathComponent().toString());
	}
	@Test
	public void activeIncludeIsSelectedForDisplayByDefault() throws Exception {
		treePanel = new IncludeHierarchyTreePanel(controller); 
		assertEquals("top",treePanel.getTree().getSelectionPath().getLastPathComponent().toString());
	}
}
