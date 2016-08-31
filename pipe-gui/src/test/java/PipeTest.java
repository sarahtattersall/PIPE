import org.junit.Before;
import org.junit.Test;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class PipeTest {


    @SuppressWarnings("unused")
    private static final int NEW = 0;

    private static final int OPEN = 1;

    private static final int CLOSE = 2;

    private PipeApplicationView applicationView;

    private JToolBar toolbar;

    private JMenu menu;

    private JMenu subMenu;

    @Before
    public void setUp() throws InvocationTargetException, InterruptedException {
        Pipe.runPipeForTesting();

        applicationView = Pipe.applicationView;
    }

    @Test
    public void verifyMenusAddedToGui() throws Exception {
        assertEquals("expecting 7 top-level menus", 7, applicationView.getJMenuBar().getMenuCount());
        menu = applicationView.getJMenuBar().getMenu(0);
        checkFileMenu();

        menu = applicationView.getJMenuBar().getMenu(1);
        checkEditMenu();

        menu = applicationView.getJMenuBar().getMenu(2);
        checkViewMenu();

        menu = applicationView.getJMenuBar().getMenu(3);
        checkDrawMenu();

        menu = applicationView.getJMenuBar().getMenu(4);
        checkInterfaceMenu();
        
        menu = applicationView.getJMenuBar().getMenu(5);
        checkAnimateMenu();

        menu = applicationView.getJMenuBar().getMenu(6);
        checkHelpMenu();
    }

	private void checkInterfaceMenu() {
		assertEquals("Interface", menu.getText());
		// see PipeApplicationBuilderTest for more details 
	}

	protected void checkHelpMenu() {
		assertEquals("Help", menu.getText());
        assertEquals("expecting 1 Help menu items", 1, menu.getItemCount());
        assertEquals("About PIPE", menu.getItem(0).getText());
	}

	protected void checkAnimateMenu() {
		assertEquals("Animate", menu.getText());
        assertEquals("expecting 5 Animate menu items, including separators", 5, menu.getItemCount());
        assertEquals("Animation mode", menu.getItem(0).getText());
        assertEquals("Back", menu.getItem(1).getText());
        assertEquals("Forward", menu.getItem(2).getText());
        assertEquals("Random", menu.getItem(3).getText());
        assertEquals("Animate", menu.getItem(4).getText());
	}

	protected void checkDrawMenu() {
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
	}

	protected void checkViewMenu() {
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
	}

	protected void checkEditMenu() {
		assertEquals("Edit", menu.getText());
        assertEquals("expecting 6 Edit menu items, including separators", 6, menu.getItemCount());
        assertEquals("Cut", menu.getItem(0).getText());
        assertEquals("Copy", menu.getItem(1).getText());
        assertEquals("Paste", menu.getItem(2).getText());
        assertEquals("Delete", menu.getItem(3).getText());
        assertEquals("Undo", menu.getItem(4).getText());
        assertEquals("Redo", menu.getItem(5).getText());
	}

	protected void checkFileMenu() {
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
	}

    @Test
    public void verifyExampleNetLoadsAndAnimates() {
        menu = applicationView.getJMenuBar().getMenu(0);
        subMenu = (JMenu) menu.getMenuComponent(11);
        assertEquals("expecting 15 example files",15,subMenu.getItemCount()); 
        selectMenuItem(subMenu, 0);
        Container c = applicationView.getContentPane();
        toolbar = (JToolBar) c.getComponent(2);
        AbstractButton animateButton = ((AbstractButton) toolbar.getComponent(20));
        animateButton.getAction().actionPerformed(null);
        //TODO verify we're animated
//        selectMenuItem(menu, 2);
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
}

