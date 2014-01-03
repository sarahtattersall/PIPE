package pipe.actions.type;

import matchers.component.HasMultiple;
import matchers.component.HasTimed;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.component.Transition;

import java.awt.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImmediateTransitionActionTest {

    private TransitionAction action;
    private PetriNetController mockController;
    private PetriNet mockNet;
    private HistoryManager mockHistory;

    @Before
    public void setUp() {
        action = new ImmediateTransitionAction("dummy", Constants.IMMTRANS, "tooltip", "key");
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        when(mockController.getPetriNet()).thenReturn(mockNet);
        mockHistory = mock(HistoryManager.class);
        when(mockController.getHistoryManager()).thenReturn(mockHistory);
    }


    @Test
    public void createsTimedTransition() {
        Point point = new Point(10, 20);

        action.doAction(point, mockController);

        verify(mockNet).addTransition(argThat(
                new HasMultiple<Transition>(
                        new HasXY(Grid.getModifiedX(point.getX()), Grid.getModifiedY(point.getY())),
                        new HasTimed(false)))
        );
    }

    @Test
    public void createsUndoAction() {
        Point point = new Point(10, 20);

        action.doAction(point, mockController);

        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }
}
