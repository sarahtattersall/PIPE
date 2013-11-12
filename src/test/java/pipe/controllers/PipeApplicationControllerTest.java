package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import pipe.gui.ApplicationSettings;
import pipe.gui.CopyPasteManager;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;


import java.io.File;

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
        PetriNetController netController = new PetriNetController();
        CopyPasteManager copyPaste = new CopyPasteManager();

        //TODO: This is a nasty fix until can remove ApplicationSettings
        PipeApplicationController nullController = null;
        ApplicationSettings.register(nullController);

        controller = new PipeApplicationController(mockModel, netController, copyPaste);
    }

    @Test
    public void registersItselfToApplicationSettings()
    {
        assertEquals(controller, ApplicationSettings.getApplicationController());
    }

    @Test
//    TODO: NEED TO CHECK THAT VIEW IS AN OBSERVER....
    public void createNewTabCreatesANewTabForEmptyPetriNet()
    {
        PipeApplicationView mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);

        when(mockModel.newPetriNetNumber()).thenReturn(1);

        controller.createNewTabFromFile(null, false);

        verify(mockView).addNewTab(eq("Petri net 1"), argThat(new ContainsEmptyPetriNet()));
    }

    @Test
    public void createNewTabCreatesANewTabForGivenPetriNetFile()
    {
        PipeApplicationView mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
        PetriNetTab mockTab = mock(PetriNetTab.class);
        when(mockView.getCurrentTab()).thenReturn(mockTab);

        File file = new File("src/test/resources/xml/simpleNet.xml");
        controller.createNewTabFromFile(file, false);
        verify(mockView).addNewTab(eq("simpleNet"), any(PetriNetTab.class));
    }

    private static class ContainsEmptyPetriNet extends ArgumentMatcher<PetriNetTab> {

        @Override
        public boolean matches(Object argument) {
            PetriNetTab petriNetTabArgument = (PetriNetTab) argument;
            if (petriNetTabArgument._petriNetView == null)
            {
                return false;
            } else {
                PetriNetView view = petriNetTabArgument._petriNetView;
                PetriNet model = view.getModel();
                return model.getAnnotations().isEmpty() &&
                       model.getTransitions().isEmpty() &&
                       model.getPlaces().isEmpty() &&
                       model.getArcs().isEmpty() &&
                       model.getRateParameters().isEmpty() &&
                       model.getTokens().isEmpty() &&
                       model.getStateGroups().isEmpty();
            }
        }
    }
}
