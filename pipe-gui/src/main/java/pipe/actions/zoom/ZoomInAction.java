package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ZoomInAction extends GuiAction {

    private final PipeApplicationController applicationController;

    public ZoomInAction(final String name, final String tooltip, final String keystroke, final PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PetriNetController currentPetriNetController = applicationController.getActivePetriNetController();
        ZoomController zoomer = currentPetriNetController.getZoomController();

        if (zoomer.canZoomIn()) {
            zoomer.zoomIn();
        }
    }
}
