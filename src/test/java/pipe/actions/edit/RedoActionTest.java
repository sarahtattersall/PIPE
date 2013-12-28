package pipe.actions.edit;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryManager;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedoActionTest {
    RedoAction redoAction;
    PipeApplicationController mockController;
    PetriNetController mockPetriNetController;

    @Before
    public void setUp()
    {
        redoAction = new RedoAction();
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
        redoAction.actionPerformed(null);
        verify(mockHistory).doRedo();
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = redoAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Redo (Ctrl-Y)", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = redoAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("ctrl Y");
        assertEquals(stroke, acceleratorKey);
    }
}
