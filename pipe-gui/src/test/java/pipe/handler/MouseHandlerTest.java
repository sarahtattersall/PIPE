package pipe.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.CreateAction;
import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.MouseUtilities;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

@RunWith(MockitoJUnitRunner.class)
public class MouseHandlerTest {
    PetriNetMouseHandler handler;

    @Mock
    PetriNetController mockController;

    @Mock
    PetriNet mockNet;

    @Mock
    PetriNetTab mockTab;

    @Mock
    MouseUtilities mockUtilities;


    @Mock
    PipeApplicationModel mockModel;

    @Mock
    MouseEvent mockEvent;

    @Mock
    CreateAction mockAction;

    @Before
    public void setup() {
        handler = new PetriNetMouseHandler(mockModel, mockController, mockTab);

        when(mockEvent.getPoint()).thenReturn(new Point(0, 0));
        when(mockUtilities.isLeftMouse(mockEvent)).thenReturn(true);
        when(mockModel.getSelectedAction()).thenReturn(mockAction);
    }

    @Test
    public void doesNoActionIfAnimating() {
        when(mockModel.isInAnimationMode()).thenReturn(true);
        handler.mousePressed(mockEvent);
        verify(mockAction, never()).doAction(any(MouseEvent.class), any(PetriNetController.class));
    }

}
