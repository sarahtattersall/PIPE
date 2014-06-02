package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.CloseWindowAction;
import pipe.controllers.application.PipeApplicationController;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CloseActionTest {
    CloseWindowAction closeWindowAction;

    @Mock
    PipeApplicationController mockController;

    @Before
    public void setUp() {
        closeWindowAction = new CloseWindowAction(mockController);
        when(mockController.hasCurrentPetriNetChanged()).thenReturn(false);
    }

    @Test
    public void actionPerformed() {
        closeWindowAction.actionPerformed(null);
        verify(mockController).removeActiveTab();
    }

    @Test
    public void setShortDescription() {
        Object shortDescription = closeWindowAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Close the current tab", shortDescription);
    }

    @Test
    public void setKeyboardShortcut() {
        Object acceleratorKey = closeWindowAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta W");
        assertEquals(stroke, acceleratorKey);
    }
}
