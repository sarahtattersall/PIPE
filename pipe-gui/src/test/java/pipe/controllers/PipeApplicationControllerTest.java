package pipe.controllers;

import org.junit.Before;
import org.junit.Test;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.actions.gui.PipeApplicationModel;

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
