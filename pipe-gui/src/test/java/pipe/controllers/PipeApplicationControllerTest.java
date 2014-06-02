package pipe.controllers;

import org.junit.Before;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.model.PipeApplicationModel;

import static org.mockito.Mockito.mock;

public class PipeApplicationControllerTest {
    PipeApplicationController applicationController;
    PipeApplicationModel mockModel;

    @Before
    public void setUp() {
        mockModel = mock(PipeApplicationModel.class);
        applicationController = new PipeApplicationController(mockModel);
   }

}
