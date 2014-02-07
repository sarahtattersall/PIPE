package pipe.actions.gui.tokens;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * Handles the combo box used to select the current token class in use.
 */
public class ChooseTokenClassAction extends GuiAction {

    private final PipeApplicationView pipeApplicationView;

    public ChooseTokenClassAction(PipeApplicationView pipeApplicationView) {
        super("chooseTokenClass", "Select current token");
        this.pipeApplicationView = pipeApplicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String currentSelection = (String) pipeApplicationView.tokenClassComboBox.getSelectedItem();
        PetriNetController petriNetController =
                ApplicationSettings.getApplicationController().getActivePetriNetController();
        try {
            petriNetController.selectToken(currentSelection);
        } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
            petriNetComponentNotFoundException.printStackTrace();
        }
    }
}
