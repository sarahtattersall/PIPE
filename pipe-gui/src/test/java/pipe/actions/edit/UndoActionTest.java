package pipe.actions.edit;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.edit.UndoAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryManager;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UndoActionTest {
    UndoAction undoAction;
    PipeApplicationController mockController;
    PetriNetController mockPetriNetController;

    @Before
    public void setUp()
    {
        undoAction = new UndoAction();
        mockController = mock(PipeApplicationController.class);
        mockPetriNetController = mock(PetriNetController.class);
        ApplicationSettings.register(mockController);
    }

    @Test
    public void actionPerformed()
    {
        HistoryManager mockHistory = mock(HistoryManager.class);
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getHistoryManager()).thenReturn(mockHistory);
        undoAction.actionPerformed(null);
        verify(mockHistory).doUndo();
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = undoAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Undo (Ctrl-Z)", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = undoAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta Z");
        assertEquals(stroke, acceleratorKey);
    }
}
