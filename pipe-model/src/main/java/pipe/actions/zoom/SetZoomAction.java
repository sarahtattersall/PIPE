package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class SetZoomAction extends GuiAction {

    private final PipeApplicationController applicationController;

    public SetZoomAction(final String name, final String tooltip, final String keystroke, final PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();

        String selection = (String) pipeApplicationView.zoomComboBox.getSelectedItem();
        validateAndZoom(selection);
    }

    private void validateAndZoom(String selection) {

        String validatedSelection = selection;

        if (selection.endsWith("%")) {
            validatedSelection = selection.substring(0, (selection.length()) - 1);
        }

        if (Integer.parseInt(validatedSelection) < Constants.ZOOM_MIN
                || Integer.parseInt(validatedSelection) > Constants.ZOOM_MAX) {
            PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
            pipeApplicationView.zoomComboBox.setSelectedItem("");
            return;
        }

        PetriNetController currentPetriNetController = applicationController.getActivePetriNetController();
        ZoomController zoomer = currentPetriNetController.getZoomController();

        if (zoomer.getPercent() != Integer.parseInt(validatedSelection)) {
            zoomer.setZoom(Integer.parseInt(validatedSelection));
        }
    }
}
