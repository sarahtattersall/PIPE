package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.file.OpenAction;
import pipe.controllers.PipeApplicationController;
import pipe.parsers.UnparsableException;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenActionTest {
    OpenAction openAction;

    @Mock
    PipeApplicationView mockView;

    @Mock
    PipeApplicationController mockController;

    @Mock
    FileDialog mockFileChooser;

    @Before
    public void setUp() {
        openAction = new OpenAction(mockController, mockView, mockFileChooser);
    }

    @Test
    public void actionPerformed() throws UnparsableException {
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.isFile()).thenReturn(true);
        when(file.canRead()).thenReturn(true);
        when(mockFileChooser.getFiles()).thenReturn(new File[] {file});

        openAction.actionPerformed(null);
        verify(mockController).createNewTabFromFile(file, mockView);
    }

    //TODO: Need to test dialog box error

    @Test
    public void setShortDescription() {
        Object shortDescription = openAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Open", shortDescription);
    }

    @Test
    public void setKeyboardShortcut() {
        Object acceleratorKey = openAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        assertEquals(stroke, acceleratorKey);
    }
}
