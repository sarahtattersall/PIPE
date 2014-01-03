package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SaveAsActionTest {
    SaveAsAction saveAsAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        saveAsAction = new SaveAsAction();
        mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
    }

    @Test
    public void actionPerformed()
    {
        saveAsAction.actionPerformed(null);
        verify(mockView).saveOperation(true);
    }


    @Test
    public void setShortDescription()
    {
        Object shortDescription = saveAsAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Save as...", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = saveAsAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("shift ctrl S");
        assertEquals(stroke, acceleratorKey);
    }
}
