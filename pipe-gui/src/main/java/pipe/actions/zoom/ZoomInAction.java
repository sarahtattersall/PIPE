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
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        PetriNetTab petriNetTab = pipeApplicationView.getCurrentTab();

        PetriNetController currentPetriNetController = applicationController.getActivePetriNetController();
        ZoomController zoomer = currentPetriNetController.getZoomController();

        boolean zoomed = zoomer.zoomIn();

        if (zoomed) {
            petriNetTab.repaint();
//            JViewport currentView = ((JScrollPane) pipeApplicationView.getFrameForPetriNetTabs().getSelectedComponent()).getViewport();
//                      petriNetTab.repaint();
//            double midpointX = ZoomController.getUnzoomedValue(currentView
//                    .getViewPosition().x
//                    + (currentView.getWidth() * 0.5), zoomer.getPercent());
//            double midpointY = ZoomController.getUnzoomedValue(currentView
//                    .getViewPosition().y
//                    + (currentView.getHeight() * 0.5), zoomer.getPercent());
//
//            pipeApplicationView.updateZoomCombo();
//            petriNetTab.zoomTo(new java.awt.Point((int) midpointX,
//                    (int) midpointY));
        }
    }
}
