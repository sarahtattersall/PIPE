package pipe.controllers.arcCreator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.arc.InboundInhibitorArc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.views.PipeApplicationView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
        Place source = new Place("", "");
        Transition transition = new Transition("", "");
        InboundArc actual = creator.createInboundArc(source, transition,
                new LinkedList<ArcPoint>());

        InboundArc expected = new InboundInhibitorArc(source, transition);
        assertEquals(expected, actual);
    }

}
