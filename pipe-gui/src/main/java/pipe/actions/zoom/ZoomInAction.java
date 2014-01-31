package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.views.PipeApplicationView;
import pipe.views.ZoomUI;

import java.awt.event.ActionEvent;

public class ZoomInAction extends GuiAction {

    private final PipeApplicationView applicationView;

    private final ZoomUI layerUI;

    public ZoomInAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView,
                        ZoomUI layerUI) {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
        this.layerUI = layerUI;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        layerUI.zoom += 0.1;
        applicationView.getTabComponent().repaint();
    }
}
