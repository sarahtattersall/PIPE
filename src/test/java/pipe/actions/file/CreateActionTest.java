package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateActionTest {
    CreateAction createAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        createAction = new CreateAction();
        mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
    }

    @Test
    public void actionPerformed()
    {
        createAction.actionPerformed(null);
        verify(mockView).createNewTabDELETEME(null, false);
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
