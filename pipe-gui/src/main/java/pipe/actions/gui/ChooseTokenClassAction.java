package pipe.actions.gui;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;

import java.awt.event.ActionEvent;

/**
 * Handles the combo box used to select the current token class in use.
 */
public class ChooseTokenClassAction extends GuiAction {

    private final PipeApplicationView pipeApplicationView;

    private final PipeApplicationController applicationController;

    public ChooseTokenClassAction(PipeApplicationView pipeApplicationView, PipeApplicationController applicationController) {
        super("chooseTokenClass", "Select current token");
        this.pipeApplicationView = pipeApplicationView;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currentSelection = (String) pipeApplicationView.tokenClassComboBox.getSelectedItem();
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        try {
            petriNetController.selectToken(currentSelection);
        } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
            petriNetComponentNotFoundException.printStackTrace();
        }
    }
}
