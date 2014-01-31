package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.views.PipeApplicationView;
import pipe.views.ZoomUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ZoomOutAction extends GuiAction {
    private final PipeApplicationView applicationView;

    private final ZoomUI layerUI;

    public ZoomOutAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView,
                        ZoomUI layerUI) {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
        this.layerUI = layerUI;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        layerUI.zoom -= 0.1;
        applicationView.getTabComponent().repaint();
    }
}
