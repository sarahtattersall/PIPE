package pipe.actions.zoom;

import pipe.actions.GuiAction;
import pipe.views.ZoomManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ZoomOutAction extends GuiAction {

    private final ZoomManager zoomManager;

    public ZoomOutAction(ZoomManager zoomManager) {
        super("Zoom out", "Zoom out by 10% ", KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.zoomManager = zoomManager;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (zoomManager.canZoomOut()) {
            zoomManager.zoomOut();
        }
    }
}
