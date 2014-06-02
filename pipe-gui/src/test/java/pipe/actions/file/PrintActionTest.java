package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.file.PrintAction;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PrintActionTest {
    PrintAction printAction;
    PipeApplicationView mockView;

    @Before
    public void setUp()
    {
        printAction = new PrintAction();
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
        Object shortDescription = printAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Print", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = printAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta P");
        assertEquals(stroke, acceleratorKey);
    }
}
