package pipe.handler;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.MouseUtilities;
import pipe.models.*;
import pipe.views.PetriNetView;

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


    TypeAction mockAction;

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
        mockAction = mock(TypeAction.class);
    }

}
