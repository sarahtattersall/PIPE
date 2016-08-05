package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the combo box user interactions for selecting tokens. This class is responsible for
 * updating controllers and models of the selected token.
 */
@SuppressWarnings("serial")
public class ChooseTokenClassAction extends GuiAction {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(ChooseTokenClassAction.class.getName());

    /**
     * Application view
     */
    private final PipeApplicationView pipeApplicationView;

    /**
     * Application controller, used to get the currently selected Petri net
     */
    private final PipeApplicationController applicationController;

    /**
     * Constructor for the action
     * @param pipeApplicationView overall application view
     * @param applicationController overalll application controller
     */
    public ChooseTokenClassAction(PipeApplicationView pipeApplicationView,
                                  PipeApplicationController applicationController) {
        super("chooseTokenClass", "Select current token");
        this.pipeApplicationView = pipeApplicationView;
        this.applicationController = applicationController;
    }

    /**
     * When a new token is selected this drop dow action selects the token in the controller
     * @param evt event 
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        String currentSelection = (String) pipeApplicationView.tokenClassComboBox.getSelectedItem();
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        try {
            petriNetController.selectToken(currentSelection);
        } catch (PetriNetComponentNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
}
