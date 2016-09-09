package pipe.actions.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.junit.Before;
import org.junit.Test;

import pipe.actions.gui.ExportPSAction;
import pipe.views.PipeApplicationView;

public class ExportPSActionTest {
    ExportPSAction exportPSAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        exportPSAction = new ExportPSAction();
        mockView = mock(PipeApplicationView.class);
    }

    @Test
    public void actionPerformed()
    {
        //TODO: Needs a re-write to be able to test static
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = exportPSAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Export the net to PostScript format", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = exportPSAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta T");
        assertEquals(stroke, acceleratorKey);
    }

}
