package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.views.ZoomManager;

import java.awt.event.ActionEvent;

public class ZoomOutAction extends GuiAction {

    private final ZoomManager zoomManager;

    public ZoomOutAction(String name, String tooltip, String keystroke, ZoomManager zoomManager) {
        super(name, tooltip, keystroke);
        this.zoomManager = zoomManager;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (zoomManager.canZoomOut()) {
            zoomManager.zoomOut();
        }
    }
}
