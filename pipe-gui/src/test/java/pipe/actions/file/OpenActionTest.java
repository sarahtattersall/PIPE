package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.file.OpenAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OpenActionTest {
    OpenAction openAction;
    PipeApplicationView mockView;
    PipeApplicationController mockController;

    @Before
    public void setUp()
    {
        mockController = mock(PipeApplicationController.class);
        mockView = mock(PipeApplicationView.class);
        openAction = new OpenAction(mockController, mockView);
    }

    @Test
    public void actionPerformed()
    {
        //TODO: Needs a re-write for testing getFile() properly so can remove
        // set test file
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.canRead()).thenReturn(true);
        openAction.setFileForTesting(mockFile);

        openAction.actionPerformed(null);
        verify(mockController).createNewTabFromFile(mockFile, mockView);
    }

    //TODO: Need to test dialog box error

    @Test
    public void setShortDescription()
    {
        Object shortDescription = openAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Open", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = openAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        assertEquals(stroke, acceleratorKey);
    }
}
