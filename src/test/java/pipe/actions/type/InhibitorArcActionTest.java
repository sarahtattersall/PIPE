package pipe.actions.type;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.component.Place;
import pipe.models.component.Transition;

import static org.mockito.Mockito.*;

public class InhibitorArcActionTest {
    private InhibitorArcAction action;
    private PetriNetController mockController;
    private PetriNet mockNet;
    private HistoryManager mockHistory;

    @Before
    public void setUp() {
        action = new InhibitorArcAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H");
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        when(mockController.getPetriNet()).thenReturn(mockNet);

        mockHistory = mock(HistoryManager.class);
        when(mockController.getHistoryManager()).thenReturn(mockHistory);
    }

    @Test
    public void createsInhibitorArcIfClickedOnPlace() {
        when(mockController.isCurrentlyCreatingArc()).thenReturn(false);

        Place place = mock(Place.class);
        action.doConnectableAction(place, mockController);

        verify(mockController).startCreatingInhibitorArc(place);
    }

    @Test
    public void doesNotCreateInhibitorArcIfClickedOnTransition() {
        when(mockController.isCurrentlyCreatingArc()).thenReturn(false);

        Transition transition = mock(Transition.class);
        action.doConnectableAction(transition, mockController);

        verify(mockController, never()).startCreatingInhibitorArc(transition);
    }

    @Test
    public void finishesCreatignArc() {
        when(mockController.isCurrentlyCreatingArc()).thenReturn(true);

        Transition transition = mock(Transition.class);
        action.doConnectableAction(transition, mockController);

        verify(mockController).finishCreatingArc(transition);
    }

}
