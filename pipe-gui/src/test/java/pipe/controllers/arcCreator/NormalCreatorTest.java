package pipe.controllers.arcCreator;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.HistoryManager;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class NormalCreatorTest {

    PipeApplicationView mockView;

    PipeApplicationController mockController;

    PetriNetController mockPetriNetController;

    HistoryManager mockHistoryManager;

    PetriNet mockNet;

    PetriNetTab mockTab;

    NormalCreator creator;

    @Before
    public void setUp() {
        mockView = mock(PipeApplicationView.class);
        mockController = mock(PipeApplicationController.class);

        mockPetriNetController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        mockTab = mock(PetriNetTab.class);
        mockHistoryManager = mock(HistoryManager.class);

        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getPetriNet()).thenReturn(mockNet);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        when(mockPetriNetController.getHistoryManager()).thenReturn(mockHistoryManager);


        creator = new NormalCreator(mockController);
    }

    @Test
    public void creatingArcAddsToPetriNet() {
        Place source = new Place("", "");
        Transition transition = new Transition("", "");
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        when(mockPetriNetController.getSelectedToken()).thenReturn(token);
        creator.create(source, transition);


        Map<Token, String> tokens = new HashMap<Token, String>();
        tokens.put(token, "1");

        Arc<Place, Transition> expected = new Arc<Place, Transition>(source, transition, tokens, ArcType.NORMAL);
        verify(mockNet).addArc(expected);
    }

    @Test
    public void creatingArcCreatesHistoryItem() {
        Place source = new Place("", "");
        Transition transition = new Transition("", "");
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        when(mockPetriNetController.getSelectedToken()).thenReturn(token);
        creator.create(source, transition);


        Map<Token, String> tokens = new HashMap<Token, String>();
        tokens.put(token, "1");

        Arc<Place, Transition> expected = new Arc<Place, Transition>(source, transition, tokens, ArcType.NORMAL);
//        HistoryItem item = new AddPetriNetObject(expected, mockNet);
//        verify(mockHistoryManager).addNewEdit(item);
    }


}
