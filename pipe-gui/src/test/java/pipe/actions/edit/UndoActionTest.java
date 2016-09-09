package pipe.actions.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.RedoAction;
import pipe.actions.gui.UndoAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;


@RunWith(MockitoJUnitRunner.class)
public class UndoActionTest {
    UndoAction undoAction;

    @Mock
    PipeApplicationController mockController;

    @Mock
    PetriNetController mockPetriNetController;

    @Mock
    UndoManager mockUndoManager;

    @Mock
    RedoAction redoAction;

    @Before
    public void setUp() {
        undoAction = new UndoAction(mockController);
    }

    @Test
    public void actionPerformed() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getUndoManager()).thenReturn(mockUndoManager);
        undoAction.actionPerformed(null);
        verify(mockUndoManager).undo();
    }

    @Test
    public void setShortDescription() {
        Object shortDescription = undoAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Undo (Ctrl-Z)", shortDescription);
    }

    @Test
    public void enablesUndoRedo() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getUndoManager()).thenReturn(mockUndoManager);

        when(mockUndoManager.canRedo()).thenReturn(false);
        when(mockUndoManager.canUndo()).thenReturn(true);
        undoAction.registerRedoAction(redoAction);
        undoAction.actionPerformed(null);

        verify(redoAction).setEnabled(false);
        verify(mockUndoManager).canRedo();
        assertTrue(undoAction.isEnabled());

    }

    @Test
    public void disablesUndoRedo() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getUndoManager()).thenReturn(mockUndoManager);

        when(mockUndoManager.canRedo()).thenReturn(true);
        when(mockUndoManager.canUndo()).thenReturn(false);

        undoAction.registerRedoAction(redoAction);
        undoAction.actionPerformed(null);

        verify(redoAction).setEnabled(true);
        verify(mockUndoManager).canRedo();
        assertFalse(undoAction.isEnabled());

    }

    @Test
    public void setKeyboardShortcut() {
        Object acceleratorKey = undoAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        assertEquals(stroke, acceleratorKey);
    }
}
