package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PipeApplicationControllerTest {
    PipeApplicationController applicationController;
    PipeApplicationModel mockModel;

    @Before
    public void setUp() {
        mockModel = mock(PipeApplicationModel.class);
        ApplicationSettings.register(mockModel);

        //TODO: This is a nasty fix until can remove ApplicationSettings
        PipeApplicationController nullController = null;
        ApplicationSettings.register(nullController);

        applicationController = new PipeApplicationController(mockModel);
    }

    @Test
    public void registersItselfToApplicationSettings()
    {
        assertEquals(applicationController, ApplicationSettings.getApplicationController());
    }


//    @Test
//    public void createsNewPetriNetControllerPerPetriNet() {
//        PipeApplicationView view = mock(PipeApplicationView.class);
//        PetriNetTab tab = applicationController.createEmptyPetriNet(view);
//        PetriNetController tabController = tab.getPetriNetController();
//        assertEquals(tabController, applicationController.getControllerForTab(tab));
//    }

}
