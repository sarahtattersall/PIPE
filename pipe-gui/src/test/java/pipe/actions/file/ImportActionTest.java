package pipe.actions.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.junit.Before;
import org.junit.Test;

import pipe.actions.gui.ImportAction;
import pipe.views.PipeApplicationView;

public class ImportActionTest {
    ImportAction importAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        importAction = new ImportAction();
        mockView = mock(PipeApplicationView.class);
    }

    @Test
    public void actionPerformed()
    {
        //TODO: Needs a re-write to be able to assure error message displayed
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = importAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Import from eDSPN", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = importAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta I");
        assertEquals(stroke, acceleratorKey);
    }
}
