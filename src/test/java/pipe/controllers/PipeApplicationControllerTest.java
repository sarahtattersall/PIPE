package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.gui.ApplicationSettings;
import pipe.gui.CopyPasteManager;
import pipe.gui.PetriNetTab;
import pipe.models.PipeApplicationModel;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PipeApplicationControllerTest {
    PipeApplicationController controller;
    PipeApplicationModel mockModel;

    @Before
    public void setUp() {
        mockModel = mock(PipeApplicationModel.class);
        ApplicationSettings.register(mockModel);
        CopyPasteManager copyPaste = new CopyPasteManager();

        //TODO: This is a nasty fix until can remove ApplicationSettings
        PipeApplicationController nullController = null;
        ApplicationSettings.register(nullController);

        controller = new PipeApplicationController(copyPaste, , mockModel);
    }

    @Test
    public void registersItselfToApplicationSettings()
    {
        assertEquals(controller, ApplicationSettings.getApplicationController());
    }


    @Test
    public void createsNewPetriNetControllerPerPetriNet() {
        PetriNetTab tab = controller.createEmptyPetriNet();
        PetriNetController tabController = tab.getPetriNetController();
        assertEquals(tabController, controller.getControllerForTab(tab));
    }

}
