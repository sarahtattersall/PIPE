package pipe.handler;

import org.junit.Before;
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

public class MouseHandlerTest {
    PetriNetController mockController;
    PetriNet mockNet;
    PetriNetTab mockTab;
    MouseUtilities mockUtilities;
    PetriNetMouseHandler handler;

    PipeApplicationModel mockModel;
    MouseEvent mockEvent;


    CreateAction mockAction;

    @Before
    public void setup() {
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        mockTab = mock(PetriNetTab.class);
        mockUtilities = mock(MouseUtilities.class);
        handler = new PetriNetMouseHandler(mockUtilities, mockController, mockNet, mockTab);

        mockModel = mock(PipeApplicationModel.class);
        mockEvent = mock(MouseEvent.class);
        when(mockEvent.getPoint()).thenReturn(new Point(0,0));
        when(mockUtilities.isLeftMouse(mockEvent)).thenReturn(true);
        mockAction = mock(CreateAction.class);
    }

}
