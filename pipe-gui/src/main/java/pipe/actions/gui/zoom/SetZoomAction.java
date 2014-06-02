package pipe.actions.gui.zoom;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class SetZoomAction extends GuiAction {

    private final PipeApplicationController applicationController;

    private final PipeApplicationView applicationView;

    public SetZoomAction(String name, String tooltip, String keystroke, PipeApplicationController applicationController,
                         PipeApplicationView applicationView) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String selection = (String) applicationView.zoomComboBox.getSelectedItem();
        validateAndZoom(selection);
    }

    private void validateAndZoom(String selection) {

        String validatedSelection = selection;

        if (selection.endsWith("%")) {
            validatedSelection = selection.substring(0, (selection.length()) - 1);
        }

        if (Integer.parseInt(validatedSelection) < Constants.ZOOM_MIN
                || Integer.parseInt(validatedSelection) > Constants.ZOOM_MAX) {
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
