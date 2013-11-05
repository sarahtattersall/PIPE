package pipe.gui;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;

import java.awt.geom.Point2D;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SelectionManagerTest {

    SelectionManager manager;
    PetriNetTab tab;
    PetriNetController mockController;

    @Before
    public void setUp() {
        mockController = mock(PetriNetController.class);
        tab = mock(PetriNetTab.class);
        manager = new SelectionManager(tab, mockController);
    }

    @Test
    public void deselectCallsController() {
        manager.clearSelection();
        verify(mockController).deselectAll();
    }

    @Test
    public void translateSelectionCallsController() {
        manager.translateSelection(5,10);
        verify(mockController).translateSelected(new Point2D.Double(5, 10));
    }
}
