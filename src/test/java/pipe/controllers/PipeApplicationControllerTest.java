package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import pipe.gui.ApplicationSettings;
import pipe.gui.CopyPasteManager;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.models.component.Token;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;


import java.awt.*;
import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        controller = new PipeApplicationController(copyPaste, mockModel);
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
