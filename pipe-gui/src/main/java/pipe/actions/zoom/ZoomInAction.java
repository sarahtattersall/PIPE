package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.views.ZoomManager;

import java.awt.event.ActionEvent;

public class ZoomInAction extends GuiAction {


    private final ZoomManager zoomManager;

    public ZoomInAction(String name, String tooltip, String keystroke, ZoomManager zoomManager) {
        super(name, tooltip, keystroke);
        this.zoomManager = zoomManager;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (zoomManager.canZoomIn()) {
            zoomManager.zoomIn();
        }
    }
}
