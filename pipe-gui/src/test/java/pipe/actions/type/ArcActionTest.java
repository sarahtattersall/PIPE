package pipe.actions.type;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.controllers.arcCreator.ArcActionCreator;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.historyActions.HistoryManager;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.visitor.connectable.arc.ArcSourceVisitor;
import pipe.views.PipeApplicationView;
import pipe.views.TemporaryArcView;

import static org.mockito.Mockito.*;

public class ArcActionTest {
    private ArcAction action;
    private PipeApplicationController mockApplicationController;
    private PetriNetController mockController;
    private PetriNet mockNet;
    private HistoryManager mockHistory;
    private PipeApplicationView mockApplicationView;
    private PetriNetTab mockTab;
    private Token activeToken;
    private ArcSourceVisitor mockSourceVisitor;
    private ArcActionCreator mockCreatorVisitor;

    @Before
    public void setUp() {
        mockSourceVisitor = mock(ArcSourceVisitor.class);
        mockCreatorVisitor = mock(ArcActionCreator.class);;
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        when(mockController.getPetriNet()).thenReturn(mockNet);

        mockHistory = mock(HistoryManager.class);
        when(mockController.getHistoryManager()).thenReturn(mockHistory);


        activeToken = mock(Token.class);
        mockApplicationView = mock(PipeApplicationView.class);
        when(mockApplicationView.getSelectedTokenName()).thenReturn("Default");

        mockTab = mock(PetriNetTab.class);
        when(mockApplicationView.getCurrentTab()).thenReturn(mockTab);


        when(mockController.getSelectedToken()).thenReturn(activeToken);

        mockApplicationController = mock(PipeApplicationController.class);
        when(mockApplicationController.getActivePetriNetController()).thenReturn(mockController);

        action = new ArcAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H", mockSourceVisitor, mockCreatorVisitor, mockApplicationController, mockApplicationView);
    }

    @Test
    public void createsTemporaryArcViewOnClick() {
        Transition transition = new Transition("","");
        when(mockSourceVisitor.canStart(transition)).thenReturn(true);
        action.doConnectableAction(transition, mockController);

        verify(mockTab).add(any(TemporaryArcView.class));
    }

    @Test
    public void removesTemporaryArcViewOnRealCreation() {
        Transition transition = new Transition("","");
        when(mockSourceVisitor.canStart(transition)).thenReturn(true);
        action.doConnectableAction(transition, mockController);


        Place place = new Place("","");
        when(mockCreatorVisitor.canCreate(transition, place)).thenReturn(true);

        action.doConnectableAction(place, mockController);
        verify(mockTab).remove(any(TemporaryArcView.class));
    }

    @Test
    public void callsCreateOnRealCreation() {
        Transition transition = new Transition("","");
        when(mockSourceVisitor.canStart(transition)).thenReturn(true);
        action.doConnectableAction(transition, mockController);


        Place place = new Place("","");
        when(mockCreatorVisitor.canCreate(transition, place)).thenReturn(true);

        action.doConnectableAction(place, mockController);
        verify(mockCreatorVisitor).create(eq(transition), eq(place), anyListOf(ArcPoint.class));
    }


    @Test
    public void updatesEndPointOnAction() {
        //TODO: WORK OUT HOW TO TEST SINCE TEMP ARC VIEW ISNT PUBLIC
    }

}
