package pipe.actions.type;


import matchers.component.HasAnnotationFields;
import matchers.component.HasMultiple;
import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.create.AnnotationAction;
import pipe.controllers.PetriNetController;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.component.annotation.Annotation;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class AnnotationActionTest {
    private PetriNetController mockController;

    private HistoryManager mockHistory;

    private PetriNet mockNet;

    private AnnotationAction action;

    @Before
    public void setUp() {
        action = new AnnotationAction();
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        when(mockController.getPetriNet()).thenReturn(mockNet);

        mockHistory = mock(HistoryManager.class);
        when(mockController.getHistoryManager()).thenReturn(mockHistory);
    }

    @Test
    public void createsAnnotationOnClick() {

        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

        verify(mockNet).addAnnotation(
                argThat(new HasMultiple<Annotation>(new HasAnnotationFields("Enter text here", 10, 20, 100, 50))));
    }

    @Test
    public void createsUndoOnClickAction() {
        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

//        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }

}
