package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.file.SaveAction;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SaveActionTest {
    SaveAction saveAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        saveAction = new SaveAction();
        mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
    }

    @Test
    public void actionPerformed()
    {
        saveAction.actionPerformed(null);
        verify(mockView).saveOperation(true);
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = saveAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Save", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = saveAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta S");
        assertEquals(stroke, acceleratorKey);
    }

}
