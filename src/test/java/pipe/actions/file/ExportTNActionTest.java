package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ExportTNActionTest {
    ExportTNAction exportTAAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        exportTAAction = new ExportTNAction();
        mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
    }

    @Test
    public void actionPerformed()
    {
        //TODO: Needs a re-write to be able to test static
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = exportTAAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Export the net to Timenet format", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = exportTAAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("ctrl E");
        assertEquals(stroke, acceleratorKey);
    }
}
