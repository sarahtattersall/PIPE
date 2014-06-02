package pipe.actions.edit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.edit.RedoAction;
import pipe.actions.gui.edit.UndoAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    UndoAction undoAction;

    @Mock
    UndoManager mockUndoManager;

    @Before
    public void setUp() {
        redoAction = new RedoAction(mockController, undoAction);
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

    @Test
    public void enablesUndoRedo() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getUndoManager()).thenReturn(mockUndoManager);
        when(mockUndoManager.canUndo()).thenReturn(true);
        when(mockUndoManager.canRedo()).thenReturn(true);
        redoAction.actionPerformed(null);
        verify(undoAction).setEnabled(true);
        assertTrue(redoAction.isEnabled());
    }


    @Test
    public void disablesUndoRedo() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getUndoManager()).thenReturn(mockUndoManager);
        when(mockUndoManager.canUndo()).thenReturn(false);
        when(mockUndoManager.canRedo()).thenReturn(false);
        redoAction.actionPerformed(null);
        verify(undoAction).setEnabled(false);
        assertFalse(redoAction.isEnabled());
    }
}
