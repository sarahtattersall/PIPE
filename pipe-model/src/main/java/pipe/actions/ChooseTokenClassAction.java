package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * @author Alex Charalambous, June 2010: Handles the combo box used to
 *         select the current token class in use.
 */
public class ChooseTokenClassAction extends GuiAction {

    public ChooseTokenClassAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e) {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        String currentSelection = (String) pipeApplicationView.tokenClassComboBox.getSelectedItem();
        PetriNetController petriNetController =
                ApplicationSettings.getApplicationController().getActivePetriNetController();
        petriNetController.selectToken(petriNetController.getToken(currentSelection));
    }
}
