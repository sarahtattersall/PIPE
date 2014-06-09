package pipe.actions.gui;

import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import pipe.controllers.ZoomController;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * Sets the drop down zoom action
 */
public class SetZoomAction extends GuiAction {

    /**
     * Main application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Main application view
     */
    private final PipeApplicationView applicationView;

    /**
     * Controller
     * @param name image name
     * @param tooltip tooltip message
     * @param keystroke keyboard short cut
     * @param applicationController main application controller
     * @param applicationView main application view
     */
    public SetZoomAction(String name, String tooltip, String keystroke, PipeApplicationController applicationController,
                         PipeApplicationView applicationView) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.applicationView = applicationView;
    }

    /**
     * Sets the zoom combo box selected percentage and performs a zoom action.
     * @param actionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String selection = (String) applicationView.zoomComboBox.getSelectedItem();
        validateAndZoom(selection);
    }

    /**
     * Validates if the zoom selection is in the format <number>% and
     * if so performs a zoom
     * @param selection zoom selection
     */
    private void validateAndZoom(String selection) {

        String validatedSelection = selection;

        if (selection.endsWith("%")) {
            validatedSelection = selection.substring(0, (selection.length()) - 1);
        }

        if (Integer.parseInt(validatedSelection) < GUIConstants.ZOOM_MIN
                || Integer.parseInt(validatedSelection) > GUIConstants.ZOOM_MAX) {
            applicationView.zoomComboBox.setSelectedItem("");
            return;
        }

        PetriNetController currentPetriNetController = applicationController.getActivePetriNetController();
        ZoomController zoomer = currentPetriNetController.getZoomController();

        if (zoomer.getPercent() != Integer.parseInt(validatedSelection)) {
            zoomer.setZoom(Integer.parseInt(validatedSelection));
        }
    }
}
