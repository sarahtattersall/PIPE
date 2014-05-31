package pipe.controllers.arcCreator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.InboundInhibitorArc;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.DiscreteTransition;
import uk.ac.imperial.pipe.models.petrinet.Transition;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InhibitorCreatorTest {

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

    InhibitorCreator creator;

    @Before
    public void setUp() {
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getPetriNet()).thenReturn(mockNet);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        creator = new InhibitorCreator();
    }

    @Test
    public void createsCorrectArc() {
        Place source = new DiscretePlace("", "");
        Transition transition = new DiscreteTransition("", "");
        InboundArc actual = creator.createInboundArc(source, transition,
                new LinkedList<ArcPoint>());

        InboundArc expected = new InboundInhibitorArc(source, transition);
        assertEquals(expected, actual);
    }

}
