package pipe.controllers.arcCreator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.InboundNormalArc;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.DiscreteTransition;
import uk.ac.imperial.pipe.models.petrinet.Transition;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

    private static final String DEFAULT_TOKEN_ID = "Default";

    @Before
    public void setUp() {

        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getPetriNet()).thenReturn(mockNet);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        creator = new NormalCreator(mockController);
    }

    @Test
    public void createsCorrectArc() {
        Place source = new DiscretePlace("", "");
        Transition transition = new DiscreteTransition("", "");
        when(mockPetriNetController.getSelectedToken()).thenReturn(DEFAULT_TOKEN_ID);
         InboundArc actual = creator.createInboundArc(source, transition, new LinkedList<ArcPoint>());


        Map<String, String> tokens = new HashMap<>();
        tokens.put(DEFAULT_TOKEN_ID, "1");

        InboundArc expected = new InboundNormalArc(source, transition, tokens);
        assertEquals(expected, actual);
    }

}
