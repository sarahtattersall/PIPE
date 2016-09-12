package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.IncludesAction;
import pipe.actions.gui.InterfaceHelpAction;
import pipe.views.PipeApplicationBuilder.PIPEComponents;

@RunWith(MockitoJUnitRunner.class)
public class PipeApplicationBuilderTest {

	private static final int ACCESS_POSITION = 0;
    private static final int INCLUDES_POSITION = 1;
	private static final int AVAILABLE_PLACES_POSITION = 2;
	private static final int SEPARATOR_POSITION = 3;
	private static final int HELP_POSITION = 4;

//	@Mock
//    PetriNetTab parent;
//
//    @Mock
//    private PipeApplicationModel model;
//    @Mock
//    private PipeApplicationView view;
//    @Mock
//    private PipeApplicationController controller;
//    @Mock
//    private ZoomUI zoomUI;

	private PipeApplicationBuilder builder;

	private JMenu menu;
	
	private PIPEComponents components;

    @Before
    public void setUp() {
    	builder = new PipeApplicationBuilder();
    	components = new PipeApplicationBuilder.PIPEComponents(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
    			new IncludesAction(null, null), new InterfaceHelpAction()); 
    	menu = builder.buildInterfaceMenu(components);
    }

    @Test
	public void verifyHasExpectedNumberOfItems() throws Exception {
    	assertEquals(5,menu.getItemCount()); 
	}
    
    @Test
    public void verifyBuildsAccessScopeMenuWithItemsDisabledAndParentsDefault() {
    	JMenu accessMenu = (JMenu) menu.getMenuComponent(ACCESS_POSITION);
		assertEquals("Place Access Scope", accessMenu.getText());
		assertEquals("Parent", accessMenu.getItem(0).getText());
		assertFalse("initially disabled",accessMenu.getItem(0).isEnabled()); 
		assertTrue(accessMenu.getItem(0) instanceof JRadioButtonMenuItem); 
		JRadioButtonMenuItem parentsButton = (JRadioButtonMenuItem) accessMenu.getItem(1); 
		assertFalse(parentsButton.isEnabled()); 
		assertEquals("Parents", parentsButton.getText());
		assertTrue(parentsButton.isSelected());
		assertEquals("Parents and Siblings", accessMenu.getItem(2).getText());
		assertFalse(accessMenu.getItem(2).isEnabled()); 
		assertEquals("All", accessMenu.getItem(3).getText());
		assertFalse(accessMenu.getItem(3).isEnabled()); 
    }
    @Test
	public void verifyIncludesAction() throws Exception {
    	JMenuItem includesItem = (JMenuItem) menu.getMenuComponent(INCLUDES_POSITION);
    	assertEquals("Includes", includesItem.getText());
    	assertTrue(includesItem.getAction() instanceof IncludesAction);

	}
    @Test
    public void verifyAvailablePlacesMenu() throws Exception {
		JMenu availablePlacesMenu = (JMenu) menu.getMenuComponent(AVAILABLE_PLACES_POSITION);
		assertEquals("Available Places", availablePlacesMenu.getText());
		assertEquals("empty to start with", 0, availablePlacesMenu.getItemCount());
    }
    @Test
    public void verifyInterfaceHelpMenu() throws Exception {
    	assertNull(menu.getItem(SEPARATOR_POSITION));
    	JMenuItem interfaceHelpItem = (JMenuItem) menu.getMenuComponent(HELP_POSITION);
    	assertEquals("Help", interfaceHelpItem.getText());
    	assertTrue(interfaceHelpItem.getAction() instanceof InterfaceHelpAction);
    }
}
