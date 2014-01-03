package pipe.handler;

import matchers.component.HasId;
import matchers.component.HasMultiple;
import matchers.component.HasTimed;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.handlers.MouseHandler;
import pipe.handlers.mouse.MouseUtilities;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.*;
import pipe.models.component.Place;
import pipe.models.component.Transition;
import pipe.views.PetriNetView;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class MouseHandlerTest {
    PetriNetController mockController;
    PetriNet mockNet;
    PetriNetTab mockTab;
    PetriNetView mockView;
    MouseUtilities mockUtilities;
    MouseHandler handler;

    PipeApplicationModel mockModel;
    MouseEvent mockEvent;


    TypeAction mockAction;

    @Before
    public void setup() {
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        mockView = mock(PetriNetView.class);
        mockTab = mock(PetriNetTab.class);
        mockUtilities = mock(MouseUtilities.class);
        handler = new MouseHandler(mockUtilities, mockController, mockNet, mockTab, mockView);

        mockModel = mock(PipeApplicationModel.class);
        mockEvent = mock(MouseEvent.class);
        when(mockEvent.getPoint()).thenReturn(new Point(0,0));
        when(mockUtilities.isLeftMouse(mockEvent)).thenReturn(true);
        mockAction = mock(TypeAction.class);
    }

    @Test
    public void callsActionDoActionMethod() {
        when(mockModel.getSelectedAction()).thenReturn(mockAction);
        ApplicationSettings.register(mockModel);

        handler.mousePressed(mockEvent);
        verify(mockAction).doAction(mockEvent.getPoint(), mockController);
    }
}
