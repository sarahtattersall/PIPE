import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pipe.actions.AnimateAction;
import pipe.actions.ChooseTokenClassAction;
import pipe.actions.DeleteAction;
import pipe.actions.EditAction;
import pipe.actions.FileAction;
import pipe.actions.GridAction;
import pipe.actions.GroupTransitionsAction;
import pipe.actions.GuiAction;
import pipe.actions.TokenAction;
import pipe.actions.TypeAction;
import pipe.actions.UnfoldAction;
import pipe.actions.UngroupTransitionsAction;
import pipe.actions.ZoomAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.TestingPlaceHandler;
import pipe.gui.TokenPanel;
import pipe.models.PipeApplicationModel;
import pipe.utilities.Copier;
import pipe.views.ArcView;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;
import pipe.views.PlaceView;
import pipe.views.TokenView;

public class PipeTest {


	@SuppressWarnings("unused")
	private static int NEW = 0;
	private static int OPEN = 1;
	private static int CLOSE = 2;
	
	private PipeApplicationView applicationView;
	private JToolBar toolbar;
	private ArrayList<PlaceView> placeViews;
	private ArrayList<ArcView> arcViews;
	private LinkedList<TokenView> tokenViews;
	private int numTokens;
	private JMenu menu;
	private JMenu subMenu;
	private File fileForTesting;
	private LinkedList<MarkingView> markingViews;
	private PlaceView placeView;
	private MarkingView markingView;
	private PetriNetView petriNetView;
	private TokenView defaultTokenView;
	private TokenView redTokenView;
	private TokenAction tokenAction;
	private TokenView greenTokenView;
	private LinkedList<MarkingView> newMarkingViews;
    @BeforeClass
	public static void setUpLog4J() throws Exception
	{
    	// to avoid the time penalty of repeated startups, start the GUI once
    	// ... but the tests will not be atomic, as there is only one GUI.
    	// if there are side effects between tests, consider separate test classes.
    	Pipe.runPipeForTesting(); 
	}
    @Before
	public void setUp() throws Exception
	{
    	applicationView = ApplicationSettings.getApplicationView();  
	}
    @Test
    public void verifyActionsAddedToGui() throws Exception
    {
    	PipeApplicationModel model = ApplicationSettings.getApplicationModel();  
    	checkAction("New", model.createAction, FileAction.class); 
    	checkAction("Open", model.openAction, FileAction.class); 
    	checkAction("Close", model.closeAction, FileAction.class); 
    	checkAction("Save", model.saveAction, FileAction.class); 
    	checkAction("Save as", model.saveAsAction, FileAction.class); 
    	checkAction("Import", model.importAction, FileAction.class); 
    	checkAction("PNG", model.exportPNGAction, FileAction.class); 
    	checkAction("PostScript", model.exportPSAction, FileAction.class); 
    	checkAction("eDSPN", model.exportTNAction, FileAction.class); 
    	checkAction("Print", model.printAction, FileAction.class); 
    	checkAction("Undo", model.undoAction, EditAction.class); 
    	checkAction("Redo", model.redoAction, EditAction.class); 
    	checkAction("Cut", model.cutAction, EditAction.class); 
    	checkAction("Copy", model.copyAction, EditAction.class); 
    	checkAction("Paste", model.pasteAction, EditAction.class); 
    	checkAction("Delete", model.deleteAction, DeleteAction.class); 
    	checkAction("Select", model.selectAction, TypeAction.class); 
    	checkAction("Place", model.placeAction, TypeAction.class); 
    	checkAction("Immediate transition", model.transAction, TypeAction.class); 
    	checkAction("Timed transition", model.timedtransAction, TypeAction.class); 
    	checkAction("Arc", model.arcAction, TypeAction.class); 
    	checkAction("Inhibitor Arc", model.inhibarcAction, TypeAction.class); 
    	checkAction("Annotation", model.annotationAction, TypeAction.class); 
    	checkAction("Add token", model.tokenAction, TypeAction.class); 
    	checkAction("Delete token", model.deleteTokenAction, TypeAction.class); 
    	checkAction("SpecifyTokenClasses", model._specifyTokenClasses, TokenAction.class); 
    	checkAction("groupTransitions", model.groupTransitions, GroupTransitionsAction.class); 
    	checkAction("ungroupTransitions", model.ungroupTransitions, UngroupTransitionsAction.class); 
    	checkAction("unfoldAction", model.unfoldAction, UnfoldAction.class); 
    	checkAction("Rate Parameter", model.rateAction, TypeAction.class); 
    	checkAction("Zoom out", model.zoomOutAction, ZoomAction.class); 
    	checkAction("Zoom in", model.zoomInAction, ZoomAction.class); 
    	checkAction("Cycle grid", model.toggleGrid, GridAction.class); 
    	checkAction("Drag", model.dragAction, TypeAction.class); 
    	checkAction("Animation mode", model.startAction, AnimateAction.class); 
    	checkAction("Back", model.stepbackwardAction, AnimateAction.class); 
    	checkAction("Forward", model.stepforwardAction, AnimateAction.class); 
    	checkAction("Random", model.randomAction, AnimateAction.class); 
    	checkAction("Animate", model.randomAnimateAction, AnimateAction.class); 
    	checkAction("chooseTokenClass", model.chooseTokenClassAction, ChooseTokenClassAction.class); 
    	checkAction("Exit", model.exitAction, FileAction.class); 
    	for (int i = 0; i < model.getZoomActions().size(); i++)
		{
    		checkAction(model.getZoomExamples()[i], model.getZoomActions().get(i), ZoomAction.class); 
		}
    	
    }
	private void checkAction(String name, GuiAction action, Class<? extends GuiAction> class1)
	{
		assertEquals(name, name, action.getValue("Name"));
		assertTrue(name, action instanceof GuiAction); 
		assertTrue(name, action.getClass().equals(class1));
	}
	@Test
	public void verifyMenusAddedToGui() throws Exception
	{
		assertEquals("expecting 6 top-level menus",6, applicationView.getJMenuBar().getMenuCount()); 
		menu = applicationView.getJMenuBar().getMenu(0); 
		assertEquals("File",menu.getText());
		assertEquals("expecting 15 File menu items, including separators",15,menu.getItemCount());
		assertEquals("New",menu.getItem(0).getText());
		assertEquals("Open",menu.getItem(1).getText());
		assertEquals("Close",menu.getItem(2).getText());
		assertNull("separator",menu.getItem(3));
		assertEquals("Save",menu.getItem(4).getText());
		assertEquals("Save as",menu.getItem(5).getText());
		assertNull("separator",menu.getItem(6));
		assertEquals("Import",menu.getItem(7).getText());
		assertEquals("Export",menu.getItem(8).getText());
		subMenu = (JMenu) menu.getMenuComponent(8); 
		assertEquals("expecting 3 Export submenu items",3,subMenu.getItemCount());
		assertEquals("PNG",subMenu.getItem(0).getText());
		assertEquals("PostScript",subMenu.getItem(1).getText());
		assertEquals("eDSPN",subMenu.getItem(2).getText());
		assertNull("separator",menu.getItem(9));
		assertEquals("Print",menu.getItem(10).getText());
		assertNull("separator",menu.getItem(11));
		assertEquals("Examples",menu.getItem(12).getText());
		subMenu = (JMenu) menu.getMenuComponent(12); 
		assertEquals("expecting 15 examples",15,subMenu.getItemCount());
		assertEquals("Accident & Emergency Unit (basic model).xml",subMenu.getItem(0).getText());
		assertEquals("Accident & Emergency Unit Coloured.xml",subMenu.getItem(1).getText());
		assertEquals("ClassicGSPN.xml",subMenu.getItem(2).getText());
		assertEquals("Coloured Reader Writer.xml",subMenu.getItem(3).getText());
		assertEquals("Courier Protocol.xml",subMenu.getItem(4).getText());
		assertEquals("Dining philosophers.xml",subMenu.getItem(5).getText());
		assertEquals("Dual Processor With Colour.xml",subMenu.getItem(6).getText());
		assertEquals("FMS1.xml",subMenu.getItem(7).getText());
		assertEquals("Producer & Consumer.xml",subMenu.getItem(8).getText());
		assertEquals("Readers & Writers.xml",subMenu.getItem(9).getText());
		assertEquals("Simple Coloured Net.xml",subMenu.getItem(10).getText());
		assertEquals("fms.xml",subMenu.getItem(11).getText());
		assertEquals("gspn1.xml",subMenu.getItem(12).getText());
		assertEquals("gspn2.xml",subMenu.getItem(13).getText());
		assertEquals("gspn3.xml",subMenu.getItem(14).getText());
		assertNull("separator",menu.getItem(13));
		assertEquals("Exit",menu.getItem(14).getText());
		
		menu = applicationView.getJMenuBar().getMenu(1); 
		assertEquals("Edit",menu.getText());
		assertEquals("expecting 7 Edit menu items, including separators",7,menu.getItemCount());
		assertEquals("Undo",menu.getItem(0).getText());
		assertEquals("Redo",menu.getItem(1).getText());
		assertNull("separator",menu.getItem(2));
		assertEquals("Cut",menu.getItem(3).getText());
		assertEquals("Copy",menu.getItem(4).getText());
		assertEquals("Paste",menu.getItem(5).getText());
		assertEquals("Delete",menu.getItem(6).getText());

		menu = applicationView.getJMenuBar().getMenu(2); 
		assertEquals("View",menu.getText());
		assertEquals("expecting 6 View menu items, including separators",6,menu.getItemCount());
		assertEquals("Zoom out",menu.getItem(0).getText());
		assertEquals("Zoom in",menu.getItem(1).getText());
		assertEquals("Zoom",menu.getItem(2).getText());
		subMenu = (JMenu) menu.getMenuComponent(2); 
		assertEquals("expecting 10 zoom levels",10,subMenu.getItemCount());
		assertEquals("40%",subMenu.getItem(0).getText());
		assertEquals("60%",subMenu.getItem(1).getText());
		assertEquals("80%",subMenu.getItem(2).getText());
		assertEquals("100%",subMenu.getItem(3).getText());
		assertEquals("120%",subMenu.getItem(4).getText());
		assertEquals("140%",subMenu.getItem(5).getText());
		assertEquals("160%",subMenu.getItem(6).getText());
		assertEquals("180%",subMenu.getItem(7).getText());
		assertEquals("200%",subMenu.getItem(8).getText());
		assertEquals("300%",subMenu.getItem(9).getText());
		assertNull("separator",menu.getItem(3));
		assertEquals("Cycle grid",menu.getItem(4).getText());
		assertEquals("Drag",menu.getItem(5).getText());

		menu = applicationView.getJMenuBar().getMenu(3); 
		assertEquals("Draw",menu.getText());
		assertEquals("expecting 17 Edit menu items, including separators",17,menu.getItemCount());
		assertEquals("Select",menu.getItem(0).getText());
		assertNull("separator",menu.getItem(1));
		assertEquals("Place",menu.getItem(2).getText());
		assertEquals("Immediate transition",menu.getItem(3).getText());
		assertEquals("Timed transition",menu.getItem(4).getText());
		assertEquals("Arc",menu.getItem(5).getText());
		assertEquals("Inhibitor Arc",menu.getItem(6).getText());
		assertEquals("Annotation",menu.getItem(7).getText());
		assertNull("separator",menu.getItem(8));
		assertEquals("Add token",menu.getItem(9).getText());
		assertEquals("Delete token",menu.getItem(10).getText());
		assertEquals("SpecifyTokenClasses",menu.getItem(11).getText());
		assertEquals("groupTransitions",menu.getItem(12).getText());
		assertEquals("ungroupTransitions",menu.getItem(13).getText());
		assertEquals("unfoldAction",menu.getItem(14).getText());
		assertNull("separator",menu.getItem(15));
		assertEquals("Rate Parameter",menu.getItem(16).getText());
		
		menu = applicationView.getJMenuBar().getMenu(4); 
		assertEquals("Animate",menu.getText());
		assertEquals("expecting 6 Animate menu items, including separators",6,menu.getItemCount());
		assertEquals("Animation mode",menu.getItem(0).getText());
		assertNull("separator",menu.getItem(1));
		assertEquals("Back",menu.getItem(2).getText());
		assertEquals("Forward",menu.getItem(3).getText());
		assertEquals("Random",menu.getItem(4).getText());
		assertEquals("Animate",menu.getItem(5).getText());

		menu = applicationView.getJMenuBar().getMenu(5); 
		assertEquals("Help",menu.getText());
		assertEquals("expecting 2 Help menu items",2,menu.getItemCount());
		assertEquals("Help",menu.getItem(0).getText());
		assertEquals("About PIPE",menu.getItem(1).getText());
	}
	@Test
	public void verifyToolbarAddedToGui() throws Exception
	{
		Container c = applicationView.getContentPane(); 
		toolbar = (JToolBar) c.getComponent(1);
		String separator = null; 
		checkButton("New", 0);
		checkButton("Open", 1);
		checkButton("Save", 2);
		checkButton("Save as", 3);
		checkButton("Close", 4);
		checkButton(separator, 5);
		checkButton("Print", 6);
		checkButton(separator, 7);
		checkButton("Cut", 8);
		checkButton("Copy", 9);
		checkButton("Paste", 10);
		checkButton("Delete", 11);
		checkButton("Undo", 12);
		checkButton("Redo", 13);
		checkButton(separator, 14);
		checkButton("Zoom out", 15);
		assertTrue("zoom combo box",toolbar.getComponent(16) instanceof JComboBox);
		checkButton("Zoom in", 17);
		checkButton(separator, 18);
		checkButton("Cycle grid", 19);
		checkButton("Drag", 20);
		checkButton("Animation mode", 21);
		checkButton(separator, 22);
		checkButton(separator, 25);
		checkButton("Help", 26);
		//stash animation toolbar
		JToolBar animationToolbar = (JToolBar) toolbar.getComponent(24);
		//back up to check drawing toolbar
		toolbar = (JToolBar) toolbar.getComponent(23);
		checkButton("Select", 0);
		checkButton(separator, 1);
		checkButton("Place", 2);
		checkButton("Immediate transition", 3);
		checkButton("Timed transition", 4);
		checkButton("Arc", 5);
		checkButton("Inhibitor Arc", 6);
		checkButton("Annotation", 7);
		checkButton(separator, 8);
		checkButton("Add token", 9);
		checkButton("Delete token", 10);
		assertTrue("token combo box",toolbar.getComponent(11) instanceof JComboBox);
		checkButton("SpecifyTokenClasses", 12);
		checkButton("groupTransitions", 13);
		checkButton("ungroupTransitions", 14);
		checkButton("unfoldAction", 15);
		checkButton(separator, 16);
		checkButton("Rate Parameter", 17);
		toolbar = animationToolbar;
		checkButton("Back", 0);
		checkButton("Forward", 1);
		checkButton("Random", 2);
		checkButton("Animate", 3);
	}
	@Test
	public void verifyExampleNetLoadsAndAnimatesFixingBug45() throws Exception
	{
		menu = applicationView.getJMenuBar().getMenu(0); 
		subMenu = (JMenu) menu.getMenuComponent(12); 
		selectMenuItem(subMenu, 0); 
		Container c = applicationView.getContentPane(); 
		toolbar = (JToolBar) c.getComponent(1);
		AbstractButton animateButton = ((AbstractButton) toolbar.getComponent(21));
		animateButton.getAction().actionPerformed(null); 
		assertTrue(applicationView.getCurrentTab().isInAnimationMode());
		selectMenuItem(menu, 2); 
	}
	@Test
	//TODO refactor
	public void verifyNetLoadsTokenViewsEditedNetSavesLoadsVerifies() throws Exception
	{
		menu = applicationView.getJMenuBar().getMenu(0); 
		subMenu = (JMenu) menu.getMenuComponent(12); 
		assertEquals("Simple Coloured Net.xml",subMenu.getItem(10).getText());
		JMenuItem item = subMenu.getItem(10);
		item.getAction().actionPerformed(null); 
		petriNetView = applicationView.getCurrentPetriNetView();
		placeViews = petriNetView.getPlacesArrayList();
		arcViews = petriNetView.getArcsArrayList(); 
		assertEquals(2, placeViews.size()); 
		assertEquals(2, arcViews.size()); 
		tokenViews = petriNetView.getTokenViews(); 
		defaultTokenView = tokenViews.get(0);
		redTokenView = tokenViews.get(1);
		placeView = placeViews.get(0);
		markingViews = placeView.getCurrentMarkingView();
		markingView = markingViews.get(0);
		assertEquals(defaultTokenView, markingView.getToken()); 
		markingView = markingViews.get(1);
		assertEquals(redTokenView, markingView.getToken()); 
		assertEquals("Default", defaultTokenView.getID());
		assertTrue(defaultTokenView.isLocked());
		assertEquals("red", redTokenView.getID());
		assertTrue(redTokenView.isLocked());
		checkTokenViews(); 
		
//		Thread.sleep(10000);  // uncomment and move around to see the GUI as needed.		
		assertEquals(2,applicationView.tokenClassComboBox.getModel().getSize());
		deleteFirstTokenFromPlaceView(); 

		menu = applicationView.getJMenuBar().getMenu(3); 
		assertEquals("update new shows 2 token Views",2,applicationView.tokenClassComboBox.getModel().getSize());

		openTokenDialogDisableDefaultTokenAddNewToken(); 
		
		newMarkingViews = placeView.getCurrentMarkingView();
		assertEquals(markingViews.get(0), newMarkingViews.get(0));
		assertEquals("Default has been dropped; no token added for Greenie, only Red left",
				1, markingViews.size()); 
		assertEquals("update drops the disabled token View",2,applicationView.tokenClassComboBox.getModel().getSize());
		assertEquals("red",applicationView.tokenClassComboBox.getModel().getElementAt(0));
		assertEquals("...adds new TokenView ","Greenie",applicationView.tokenClassComboBox.getModel().getElementAt(1));
		markingView = markingViews.get(0);
		assertEquals(redTokenView, markingView.getToken()); 
		tokenViews = petriNetView.getTokenViews(); 
		greenTokenView = tokenViews.get(1); 
		assertEquals(2, tokenViews.size()); 
		assertEquals("Greenie", greenTokenView.getID()); 
		assertFalse(defaultTokenView.isEnabled());
		menu = applicationView.getJMenuBar().getMenu(0);
		savePetriNet();
		
		selectMenuItem(menu, CLOSE); 
		
		reloadPetriNet();
		petriNetView = applicationView.getCurrentPetriNetView();
		placeViews = petriNetView.getPlacesArrayList();
		assertEquals(2, placeViews.size()); 
		placeView = placeViews.get(0);
		markingViews = placeView.getCurrentMarkingView();
		markingView = markingViews.get(0);
		tokenViews = petriNetView.getTokenViews(); 
		assertEquals("Default has been dropped; empty marking view for Greenie, Red has 1, is locked",
				2, markingViews.size());
		assertEquals("first non-zero marking view was red, but greenie was loaded as first token when net built",
				markingView.getToken(), tokenViews.get(1)); 
		markingView = markingViews.get(1);
		assertEquals(markingView.getToken(), tokenViews.get(0)); 
		assertEquals(2,applicationView.tokenClassComboBox.getModel().getSize());
		assertEquals("Greenie",applicationView.tokenClassComboBox.getModel().getElementAt(0));
		assertEquals("red",applicationView.tokenClassComboBox.getModel().getElementAt(1));
		assertEquals(2, tokenViews.size()); 
		assertEquals("Greenie", tokenViews.get(0).getID()); 
		assertFalse(tokenViews.get(0).isLocked()); 
		assertEquals("red", tokenViews.get(1).getID()); 
		assertTrue(tokenViews.get(1).isLocked()); 
	}
	protected void showTokenDialogAndJustClickOk()
	{
		tokenAction = (TokenAction) getActionForMenuItem(menu, 11);  
		tokenAction.forceOkForTesting();
		tokenAction.actionPerformed(null);
	}
	protected void deleteFirstTokenFromPlaceView()
	{
		markingViews = placeView.getCurrentMarkingView();
		markingView = markingViews.get(0);
		assertEquals(1, markingViews.get(0).getCurrentMarking() ); 
		assertEquals(1, markingViews.get(1).getCurrentMarking() ); 
		TestingPlaceHandler handler = new TestingPlaceHandler(null, placeView);
        LinkedList<MarkingView> oldMarkingViews = Copier.mediumCopy(placeView.getCurrentMarkingView());
		handler.deleteTokenForTesting(oldMarkingViews, applicationView.getCurrentHistoryManager()); 
		newMarkingViews = placeView.getCurrentMarkingView();
		assertEquals(markingViews.get(0), newMarkingViews.get(0));
		assertEquals(markingViews.get(1), newMarkingViews.get(1));
		assertEquals(0, markingViews.get(0).getCurrentMarking() ); 
		assertEquals(1, markingViews.get(1).getCurrentMarking() );
		assertEquals(0, defaultTokenView.getCurrentMarking());
		assertFalse(defaultTokenView.isLocked());
		assertTrue(redTokenView.isLocked());
	}
	private void reloadPetriNet()
	{
		FileAction action = (FileAction) getActionForMenuItem(menu,OPEN);
		action.setFileForTesting(fileForTesting); 
		action.actionPerformed(null); 
	}
	private void savePetriNet()
	{
		fileForTesting = new File("PipeTestFile.xml"); 
		if (fileForTesting.exists()) fileForTesting.delete(); 
//		System.out.println(fileForTesting.getAbsolutePath());
		applicationView.saveNet(fileForTesting, false); 
	}
	protected void openTokenDialogDisableDefaultTokenAddNewToken() throws InterruptedException
	{ // To get access to the table in TokenPanel, TokenAction.actionPerformed refactored to three methods:
	  // buildTokenGuiClasses(), finishBuildingGui(), updateTokenViewsFromGui(); invoked separately.  
		tokenAction = (TokenAction) getActionForMenuItem(menu, 11);  
		tokenAction.forceOkForTesting();
		tokenAction.buildTokenGuiClasses();
		TokenPanel dialogContent = tokenAction.getDialogContentForTesting(); 
		assertEquals(true, dialogContent.table.getModel().getValueAt(0, 0)); 
		dialogContent.table.getModel().setValueAt(false, 0, 0); // disable Default tokenview
		dialogContent.table.getModel().setValueAt(true, 2, 0); // disable Default tokenview
		dialogContent.table.getModel().setValueAt("Greenie", 2, 1); // disable Default tokenview
		dialogContent.table.getModel().setValueAt(Color.green, 2, 2); // disable Default tokenview
		assertEquals(false, dialogContent.table.getModel().getValueAt(0, 0)); 
		tokenAction.finishBuildingGui();
		tokenAction.updateTokenViewsFromGui(); 
	}
	private Action selectMenuItem(JMenu menu, int selection)
	{
		Action action = getActionForMenuItem(menu, selection); 
		action.actionPerformed(null);
		return action;
	}
	protected Action getActionForMenuItem(JMenu menu, int selection)
	{
		JMenuItem item = menu.getItem(selection); 
		Action action = item.getAction();
		return action;
	}
	private void checkTokenViews()
	{
		numTokens = tokenViews.size(); 
		for (int i = 0; i < numTokens; i++)
		{
			checkPlaces(tokenViews.get(i), i);
			checkArcs(tokenViews.get(i), i);
		}
	}
	private void checkPlaces(TokenView tokenView, int i)
	{
		for (PlaceView placeView : placeViews)
		{
			assertEquals(numTokens, placeView.getCurrentMarkingView().size());
			assertEquals(tokenView, placeView.getCurrentMarkingView().get(i).getToken()); 
		}
	}
	private void checkArcs(TokenView tokenView, int i)
	{
		for (ArcView arcView : arcViews)
		{
			assertEquals(numTokens, arcView.getWeight().size());
			assertEquals(tokenView, arcView.getWeight().get(i).getToken()); 
		}
	}
	private void checkButton(String name, int index)
	{
		if (name == null) 
		{
			assertTrue("expecting separator",(toolbar.getComponent(index) instanceof javax.swing.JToolBar.Separator));
		}
		else
		{
			AbstractButton button = ((AbstractButton) toolbar.getComponent(index)); 
			assertEquals(name,name,((GuiAction) button.getAction()).getValue("Name"));
		}
	}
}

