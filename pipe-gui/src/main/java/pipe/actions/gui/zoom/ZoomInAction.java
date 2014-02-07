package pipe.actions.gui.zoom;

import pipe.actions.gui.GuiAction;
import pipe.views.ZoomManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ZoomInAction extends GuiAction {


    private final ZoomManager zoomManager;

    public ZoomInAction(ZoomManager zoomManager) {
        super("Zoom in", "Zoom in by 10% ", KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.zoomManager = zoomManager;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (zoomManager.canZoomIn()) {
            zoomManager.zoomIn();
        }
    }
}
