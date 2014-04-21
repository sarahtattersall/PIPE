package pipe.actions.gui.animate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.GUIAnimator;
import pipe.gui.model.PipeApplicationModel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToggleAnimateActionTest {


    private ToggleAnimateAction action;

    @Mock
    private PipeApplicationController controller;

    @Mock
    private PetriNetController petriNetController;

    @Mock
    private PipeApplicationModel model;

    @Mock
    private GUIAnimator animator;

    @Before
    public void setUp() {
        action = new ToggleAnimateAction("Toggle", "Tooltip", "T", model, controller);
        when(controller.getActivePetriNetController()).thenReturn(petriNetController);
        when(petriNetController.getAnimator()).thenReturn(animator);
    }

    @Test
    public void startsAnimation() {
        when(petriNetController.toggleAnimation()).thenReturn(true);
        action.actionPerformed(null);
        verify(animator).startAnimation();
    }


    @Test
    public void finishesAnimation() {
        when(petriNetController.toggleAnimation()).thenReturn(false);
        action.actionPerformed(null);
        verify(animator).finish();
    }

    @Test
    public void togglesAnimation(){
        when(petriNetController.toggleAnimation()).thenReturn(false);
        action.actionPerformed(null);
        verify(petriNetController).toggleAnimation();
    }
}
