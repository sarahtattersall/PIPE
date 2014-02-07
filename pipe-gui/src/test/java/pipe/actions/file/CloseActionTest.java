package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.file.CloseWindowAction;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CloseActionTest {
    CloseWindowAction closeWindowAction;

    PipeApplicationView mockView;

    @Before
    public void setUp() {
        mockView = mock(PipeApplicationView.class);
        closeWindowAction = new CloseWindowAction(mockView);
    }

    @Test
    public void actionPerformed() {
        int selectedIndex = 20;
        JTabbedPane mockpane = mock(JTabbedPane.class);
        when(mockpane.getTabCount()).thenReturn(1);
        when(mockpane.getSelectedIndex()).thenReturn(selectedIndex);
        when(mockView.getFrameForPetriNetTabs()).thenReturn(mockpane);
        closeWindowAction.actionPerformed(null);
        verify(mockView).removeCurrentTab();
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
