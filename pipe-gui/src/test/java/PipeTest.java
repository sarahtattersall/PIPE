import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pipe.actions.gui.GuiAction;
import pipe.actions.gui.tokens.SpecifyTokenAction;
import pipe.gui.ApplicationSettings;
import pipe.views.*;

import javax.swing.*;
import java.awt.Container;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PipeTest {


    @SuppressWarnings("unused")
    private static final int NEW = 0;

    private static final int OPEN = 1;

    private static final int CLOSE = 2;

    private PipeApplicationView applicationView;

    private JToolBar toolbar;

    private Collection<PlaceView> placeViews;

    private Collection<ArcView> arcViews;

    private LinkedList<TokenView> tokenViews;

    private int numTokens;

    private JMenu menu;

    private JMenu subMenu;

    private File fileForTesting;

    private List<MarkingView> markingViews;

    private PlaceView placeView;

    private MarkingView markingView;

    private PetriNetView petriNetView;

    private TokenView defaultTokenView;

    private TokenView redTokenView;

    private SpecifyTokenAction tokenAction;

    private TokenView greenTokenView;

    private List<MarkingView> newMarkingViews;

    @BeforeClass
    public static void setUpLog4J() throws Exception {
        // to avoid the time penalty of repeated startups, start the GUI once
        // ... but the tests will not be atomic, as there is only one GUI.
        // if there are side effects between tests, consider separate test classes.
        Pipe.runPipeForTesting();
    }

    @Before
    public void setUp() throws Exception {
        applicationView = ApplicationSettings.getApplicationView();
    }

    @Test
    public void verifyMenusAddedToGui() throws Exception {
        assertEquals("expecting 6 top-level menus", 6, applicationView.getJMenuBar().getMenuCount());
        menu = applicationView.getJMenuBar().getMenu(0);
        assertEquals("File", menu.getText());
        assertEquals("expecting 14 File menu items, including separators", 14, menu.getItemCount());
        assertEquals("New", menu.getItem(0).getText());
        assertEquals("Open", menu.getItem(1).getText());
        assertEquals("Save", menu.getItem(2).getText());
        assertEquals("Save as", menu.getItem(3).getText());
        assertEquals("Close", menu.getItem(4).getText());
        assertNull("separator", menu.getItem(5));
        assertEquals("Import", menu.getItem(6).getText());
        assertEquals("Export", menu.getItem(7).getText());
        subMenu = (JMenu) menu.getMenuComponent(7);
        assertEquals("expecting 3 Export submenu items", 3, subMenu.getItemCount());
        assertEquals("PNG", subMenu.getItem(0).getText());
        assertEquals("PostScript", subMenu.getItem(1).getText());
        assertEquals("eDSPN", subMenu.getItem(2).getText());
        assertEquals("Print", menu.getItem(9).getText());
        assertNull("separator", menu.getItem(10));
        assertEquals("Examples", menu.getItem(11).getText());
        subMenu = (JMenu) menu.getMenuComponent(11);
        assertEquals("expecting 15 examples", 15, subMenu.getItemCount());
        assertEquals("Accident & Emergency Unit (basic model).xml", subMenu.getItem(0).getText());
        assertEquals("Accident & Emergency Unit Coloured.xml", subMenu.getItem(1).getText());
        assertEquals("ClassicGSPN.xml", subMenu.getItem(2).getText());
        assertEquals("Coloured Reader Writer.xml", subMenu.getItem(3).getText());
        assertEquals("Courier Protocol.xml", subMenu.getItem(4).getText());
        assertEquals("Dining philosophers.xml", subMenu.getItem(5).getText());
        assertEquals("Dual Processor With Colour.xml", subMenu.getItem(6).getText());
        assertEquals("FMS1.xml", subMenu.getItem(7).getText());
        assertEquals("Producer & Consumer.xml", subMenu.getItem(8).getText());
        assertEquals("Readers & Writers.xml", subMenu.getItem(9).getText());
        assertEquals("Simple Coloured Net.xml", subMenu.getItem(10).getText());
        assertEquals("fms.xml", subMenu.getItem(11).getText());
        assertEquals("gspn1.xml", subMenu.getItem(12).getText());
        assertEquals("gspn2.xml", subMenu.getItem(13).getText());
        assertEquals("gspn3.xml", subMenu.getItem(14).getText());
        assertEquals("Exit", menu.getItem(13).getText());

        menu = applicationView.getJMenuBar().getMenu(1);
        assertEquals("Edit", menu.getText());
        assertEquals("expecting 6 Edit menu items, including separators", 6, menu.getItemCount());
        assertEquals("Cut", menu.getItem(0).getText());
        assertEquals("Copy", menu.getItem(1).getText());
        assertEquals("Paste", menu.getItem(2).getText());
        assertEquals("Delete", menu.getItem(3).getText());
        assertEquals("Undo", menu.getItem(4).getText());
        assertEquals("Redo", menu.getItem(5).getText());


        menu = applicationView.getJMenuBar().getMenu(2);
        assertEquals("View", menu.getText());
        assertEquals("expecting 6 View menu items, including separators", 5, menu.getItemCount());
        assertEquals("Zoom out", menu.getItem(0).getText());
        assertEquals("Zoom in", menu.getItem(1).getText());
        assertEquals("Zoom", menu.getItem(2).getText());
        subMenu = (JMenu) menu.getMenuComponent(2);
        assertEquals("expecting 10 zoom levels", 10, subMenu.getItemCount());
        assertEquals("40%", subMenu.getItem(0).getText());
        assertEquals("60%", subMenu.getItem(1).getText());
        assertEquals("80%", subMenu.getItem(2).getText());
        assertEquals("100%", subMenu.getItem(3).getText());
        assertEquals("120%", subMenu.getItem(4).getText());
        assertEquals("140%", subMenu.getItem(5).getText());
        assertEquals("160%", subMenu.getItem(6).getText());
        assertEquals("180%", subMenu.getItem(7).getText());
        assertEquals("200%", subMenu.getItem(8).getText());
        assertEquals("300%", subMenu.getItem(9).getText());
        assertNull("separator", menu.getItem(3));
        assertEquals("Cycle grid", menu.getItem(4).getText());

        menu = applicationView.getJMenuBar().getMenu(3);
        assertEquals("Draw", menu.getText());
        assertEquals("expecting 15 Edit menu items, including separators", 15, menu.getItemCount());
        assertEquals("Select", menu.getItem(0).getText());
        assertNull("separator", menu.getItem(1));
        assertEquals("Place", menu.getItem(2).getText());
        assertEquals("Immediate transition", menu.getItem(3).getText());
        assertEquals("Timed transition", menu.getItem(4).getText());
        assertEquals("Arc", menu.getItem(5).getText());
        assertEquals("Inhibitor Arc", menu.getItem(6).getText());
        assertEquals("Annotation", menu.getItem(7).getText());
        assertEquals("Rate Parameter", menu.getItem(8).getText());
        assertEquals("Add token", menu.getItem(10).getText());
        assertEquals("Delete token", menu.getItem(11).getText());
        assertEquals("SpecifyTokenClasses", menu.getItem(12).getText());
        assertEquals("unfoldAction", menu.getItem(13).getText());
        assertNull("separator", menu.getItem(14));

        menu = applicationView.getJMenuBar().getMenu(4);
        assertEquals("Animate", menu.getText());
        assertEquals("expecting 5 Animate menu items, including separators", 5, menu.getItemCount());
        assertEquals("Animation mode", menu.getItem(0).getText());
        assertEquals("Back", menu.getItem(1).getText());
        assertEquals("Forward", menu.getItem(2).getText());
        assertEquals("Random", menu.getItem(3).getText());
        assertEquals("Animate", menu.getItem(4).getText());

        menu = applicationView.getJMenuBar().getMenu(5);
        assertEquals("Help", menu.getText());
        assertEquals("expecting 2 Help menu items", 2, menu.getItemCount());
        assertEquals("Help", menu.getItem(0).getText());
        assertEquals("About PIPE", menu.getItem(1).getText());
    }

    @Test
    public void verifyExampleNetLoadsAndAnimates() {
        menu = applicationView.getJMenuBar().getMenu(0);
        subMenu = (JMenu) menu.getMenuComponent(11);
        selectMenuItem(subMenu, 0);
        Container c = applicationView.getContentPane();
        toolbar = (JToolBar) c.getComponent(1);
        AbstractButton animateButton = ((AbstractButton) toolbar.getComponent(20));
        animateButton.getAction().actionPerformed(null);
        selectMenuItem(menu, 2);
    }

    private Action selectMenuItem(JMenu menu, int selection) {
        Action action = getActionForMenuItem(menu, selection);
        action.actionPerformed(null);
        return action;
    }

    protected Action getActionForMenuItem(JMenu menu, int selection) {
        JMenuItem item = menu.getItem(selection);
        return item.getAction();
    }

    private void checkTokenViews() {
        numTokens = tokenViews.size();
        for (int i = 0; i < numTokens; i++) {
            checkPlaces(tokenViews.get(i), i);
            checkArcs(tokenViews.get(i), i);
        }
    }

    private void checkPlaces(TokenView tokenView, int i) {
        for (PlaceView placeView : placeViews) {
            assertEquals(numTokens, placeView.getCurrentMarkingView().size());
            assertEquals(tokenView, placeView.getCurrentMarkingView().get(i).getToken());
        }
    }

    private void checkArcs(TokenView tokenView, int i) {
        for (ArcView arcView : arcViews) {
            assertEquals(numTokens, arcView.getWeight().size());
            List<MarkingView> markings = arcView.getWeight();
            MarkingView marking = markings.get(i);
            assertEquals(tokenView, marking.getToken());
        }
    }

    private void checkButton(String name, int index) {
        if (name == null) {
            assertTrue("expecting separator", (toolbar.getComponent(index) instanceof javax.swing.JToolBar.Separator));
        } else {
            AbstractButton button = ((AbstractButton) toolbar.getComponent(index));
            assertEquals(name, name, ((GuiAction) button.getAction()).getValue("Name"));
        }
    }
}

