package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateActionTest {
    CreateAction createAction;
    PipeApplicationController mockController;

    @Before
    public void setUp()
    {
        createAction = new CreateAction();
        mockController = mock(PipeApplicationController.class);
        ApplicationSettings.register(mockController);
    }

    @Test
    public void actionPerformed()
    {
        createAction.actionPerformed(null);
        verify(mockController).createEmptyPetriNet();
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = createAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Create a new Petri net", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = createAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("ctrl N");
        assertEquals(stroke, acceleratorKey);
    }
}
