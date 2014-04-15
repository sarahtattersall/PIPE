package pipe.handler;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.create.CreateAction;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.MouseUtilities;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MouseHandlerTest {
    @Mock
    PetriNetController mockController;
    @Mock
    PetriNet mockNet;
    @Mock
    PetriNetTab mockTab;
    @Mock
    MouseUtilities mockUtilities;

    PetriNetMouseHandler handler;

    @Mock
    PipeApplicationModel mockModel;
    @Mock
    MouseEvent mockEvent;

    @Mock
    CreateAction mockAction;

    @Before
    public void setup() {
        handler = new PetriNetMouseHandler(mockUtilities, mockController, mockTab);

        when(mockEvent.getPoint()).thenReturn(new Point(0,0));
        when(mockUtilities.isLeftMouse(mockEvent)).thenReturn(true);
        mockAction = mock(CreateAction.class);
    }

}
