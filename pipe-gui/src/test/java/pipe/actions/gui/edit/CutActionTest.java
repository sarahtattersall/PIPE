package pipe.actions.gui.edit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.CutAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

@RunWith(MockitoJUnitRunner.class)
public class CutActionTest {
    private CutAction cutAction;

    @Mock
    private PipeApplicationController applicationController;
    @Mock
    private PetriNetController petriNetController;

    @Mock
    private UndoableEditListener listener;

    @Mock
    private PetriNetComponent component;

    @Before
    public void setUp() {
        cutAction = new CutAction(applicationController);
        when(applicationController.getActivePetriNetController()).thenReturn(petriNetController);
    }

    @Test
    public void doesNotCutIfNoneSelected() {
        when(petriNetController.getSelectedComponents()).thenReturn(new HashSet<PetriNetComponent>());

        cutAction.actionPerformed(null);

        verify(petriNetController, never()).copySelection();
    }


    @Test
    public void doesNotUndoIfNoneSelected() {
        when(petriNetController.getSelectedComponents()).thenReturn(new HashSet<PetriNetComponent>());
        cutAction.addUndoableEditListener(listener);

        cutAction.actionPerformed(null);

        verify(listener, never()).undoableEditHappened(any(UndoableEditEvent.class));
    }

    @Test
    public void performsCutIfSelected() throws PetriNetComponentException {
        Set<PetriNetComponent> components = new HashSet<>();
        components.add(component);
        when(petriNetController.getSelectedComponents()).thenReturn(components);

        cutAction.actionPerformed(null);

        verify(petriNetController).copySelection();
        verify(petriNetController).deleteSelection();

    }


    @Test
    public void performsUndoItemIfSelected() {
        Set<PetriNetComponent> components = new HashSet<>();
        components.add(component);
        when(petriNetController.getSelectedComponents()).thenReturn(components);

        cutAction.addUndoableEditListener(listener);
        cutAction.actionPerformed(null);

        verify(listener).undoableEditHappened(any(UndoableEditEvent.class));

    }
}