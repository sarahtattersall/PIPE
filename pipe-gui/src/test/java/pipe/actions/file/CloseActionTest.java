package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.views.PipeApplicationView;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CloseActionTest {
    CloseAction closeAction;

    PipeApplicationView mockView;

    @Before
    public void setUp() {
        closeAction = new CloseAction(mockView);
        mockView = mock(PipeApplicationView.class);
    }

    @Test
    public void actionPerformed() {
        int selectedIndex = 20;
        JTabbedPane mockpane = mock(JTabbedPane.class);
        when(mockpane.getTabCount()).thenReturn(1);
        when(mockpane.getSelectedIndex()).thenReturn(selectedIndex);
        when(mockView.getFrameForPetriNetTabs()).thenReturn(mockpane);
        closeAction.actionPerformed(null);
        verify(mockView).removeTab(selectedIndex);
        verify(mockpane).remove(selectedIndex);
    }

    @Test
    public void setShortDescription() {
        Object shortDescription = closeAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Close the current tab", shortDescription);
    }

    @Test
    public void setKeyboardShortcut() {
        Object acceleratorKey = closeAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("ctrl W");
        assertEquals(stroke, acceleratorKey);
    }
}
