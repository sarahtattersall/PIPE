package pipe.actions.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.OpenAction;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;

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
        openAction = new OpenAction(mockController, mockFileChooser);
    }

    @Test
    public void actionPerformed() throws Exception {
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.isFile()).thenReturn(true);
        when(file.canRead()).thenReturn(true);
        when(mockFileChooser.getFiles()).thenReturn(new File[] {file});

        openAction.actionPerformed(null);
        verify(mockController).createNewTabFromFile(file);
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
