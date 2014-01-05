package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ZoomOutAction extends GuiAction {
    public ZoomOutAction(final String name, final String tooltip, final String keystroke) {
        super(name, tooltip, keystroke);
    }


    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PipeApplicationView _pipeApplicationView = ApplicationSettings.getApplicationView();
        PetriNetTab appView = _pipeApplicationView.getCurrentTab();
        ZoomController zoomer = appView.getZoomController();

        boolean zoomed = zoomer.zoomOut();

        if (zoomed) {
            JViewport currentView = ((JScrollPane) _pipeApplicationView.getFrameForPetriNetTabs().getSelectedComponent()).getViewport();

            double midpointX = ZoomController.getUnzoomedValue(currentView
                    .getViewPosition().x
                    + (currentView.getWidth() * 0.5), zoomer.getPercent());
            double midpointY = ZoomController.getUnzoomedValue(currentView
                    .getViewPosition().y
                    + (currentView.getHeight() * 0.5), zoomer.getPercent());

            _pipeApplicationView.updateZoomCombo();
            appView.zoomTo(new java.awt.Point((int) midpointX,
                    (int) midpointY));
        }
    }
}
