package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import pipe.gui.ApplicationSettings;
import pipe.gui.CopyPasteManager;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.models.Token;
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

        controller = new PipeApplicationController(mockModel, copyPaste);
    }

    @Test
    public void registersItselfToApplicationSettings()
    {
        assertEquals(controller, ApplicationSettings.getApplicationController());
    }

    @Test
//    TODO: NEED TO CHECK THAT VIEW IS AN OBSERVER....
    public void createNewTabCreatesANewTabForPetriNetWithDefaultToken()
    {
        PipeApplicationView mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);

        when(mockModel.newPetriNetNumber()).thenReturn(1);

        controller.createEmptyPetriNet();

        verify(mockView).addNewTab(eq("Petri net 1"), argThat(new ContainsPetriNetWithDefaultToken()));
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

    @Test
    public void createsNewPetriNetControllerPerPetriNet() {
        PetriNetTab tab = controller.createEmptyPetriNet();
        PetriNetController tabController = tab.getPetriNetController();
        assertEquals(tabController, controller.getControllerForTab(tab));
    }

    private static class ContainsPetriNetWithDefaultToken extends ArgumentMatcher<PetriNetTab> {

        @Override
        public boolean matches(Object argument) {
            PetriNetTab petriNetTabArgument = (PetriNetTab) argument;
            if (petriNetTabArgument._petriNetView == null)
            {
                return false;
            } else {
                PetriNetView view = petriNetTabArgument._petriNetView;
                PetriNet model = view.getModel();
                Collection<Token> tokens = model.getTokens();
                if(tokens.isEmpty() || tokens.size() > 1) {
                    return false;
                }

                Token token = tokens.iterator().next();
                return token.isEnabled() &&
                        token.getId().equals("Default") &&
                        token.getColor().equals(Color.BLACK) &&
                        model.getAnnotations().isEmpty() &&
                        model.getTransitions().isEmpty() &&
                        model.getPlaces().isEmpty() &&
                        model.getArcs().isEmpty() &&
                        model.getRateParameters().isEmpty() &&
                        model.getTokens().size() == 1 &&
                        model.getStateGroups().isEmpty();
            }
        }
    }
}
