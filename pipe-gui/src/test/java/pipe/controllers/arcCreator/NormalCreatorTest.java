package pipe.controllers.arcCreator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.views.PipeApplicationView;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NormalCreatorTest {

    @Mock
    PipeApplicationView mockView;

    @Mock
    PipeApplicationController mockController;

    @Mock
    PetriNetController mockPetriNetController;

    @Mock
    PetriNet mockNet;

    @Mock
    PetriNetTab mockTab;

    NormalCreator creator;

    @Before
    public void setUp() {

        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getPetriNet()).thenReturn(mockNet);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        creator = new NormalCreator(mockController);
    }

    @Test
    public void createsCorrectArc() {
        Place source = new Place("", "");
        Transition transition = new Transition("", "");
        Token token = new Token("Default", new Color(0, 0, 0));
        when(mockPetriNetController.getSelectedToken()).thenReturn(token);
        Arc<? extends Connectable, ? extends Connectable> actual = creator.create(source, transition);


        Map<Token, String> tokens = new HashMap<>();
        tokens.put(token, "1");

        Arc<Place, Transition> expected = new Arc<>(source, transition, tokens, ArcType.NORMAL);
        assertEquals(expected, actual);
    }

}
