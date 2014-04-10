package pipe.actions.edit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.edit.RedoAction;
import pipe.actions.manager.ComponentEditorManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RedoActionTest {
    RedoAction redoAction;

    @Mock
    PipeApplicationController mockController;

    @Mock
    PetriNetController mockPetriNetController;

    @Mock
    ComponentEditorManager container;

    @Mock
    UndoManager mockUndoManager;

    @Before
    public void setUp() {
        redoAction = new RedoAction(mockController, container);
    }

    @Test
    public void actionPerformed() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getUndoManager()).thenReturn(mockUndoManager);
        redoAction.actionPerformed(null);
        verify(mockUndoManager).redo();
    }

    @Test
    public void setShortDescription() {
        Object shortDescription = redoAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Redo (Ctrl-Y)", shortDescription);
    }

    @Test
    public void setKeyboardShortcut() {
        Object acceleratorKey = redoAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        assertEquals(stroke, acceleratorKey);
    }
}
