package pipe.models.visitor.connectable.arc;

import org.junit.Before;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InhibitorCreatorVisitorTest {
    InhibitorCreatorVisitor visitor;
    PetriNetController mockPetriNetController;

    @Before
    public void setUp() {
        PipeApplicationController mockController = mock(PipeApplicationController.class);
        mockPetriNetController = mock(PetriNetController.class);
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);

        visitor = new InhibitorCreatorVisitor(mockController);
    }


}
