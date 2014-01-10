package pipe.controllers.arcCreator;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.models.component.Transition;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.views.ArcView;
import pipe.views.NormalArcView;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class NormalCreatorTest {

    PipeApplicationView mockView;
    PipeApplicationController mockController;
    PetriNetController mockPetriNetController;
    PetriNet mockNet;
    PetriNetTab mockTab;
    NormalCreator creator;

    @Before
    public void setUp() {
        mockView = mock(PipeApplicationView.class);
        mockController = mock(PipeApplicationController.class);

        mockPetriNetController = mock(PetriNetController.class);
        ArcStrategy<Place, Transition> mockStrategy = mock(BackwardsNormalStrategy.class);
        when(mockPetriNetController.getBackwardsStrategy()).thenReturn(mockStrategy);
        mockNet = mock(PetriNet.class);
        mockTab = mock(PetriNetTab.class);

        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getPetriNet()).thenReturn(mockNet);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        creator = new NormalCreator(mockController, mockView);
    }

    @Test
    public void creatingArcAddsToPetriNet() {
        Place source = new Place("", "");
        Transition transition = new Transition("", "");
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        creator.create(source, transition);


        Map<Token, String> tokens = new HashMap<Token, String>();
        tokens.put(token, "1");

        Arc<Place, Transition> expected =
                new Arc<Place, Transition>(source, transition, tokens, mockPetriNetController.getBackwardsStrategy());
        verify(mockNet).addArc(expected);
    }

}
