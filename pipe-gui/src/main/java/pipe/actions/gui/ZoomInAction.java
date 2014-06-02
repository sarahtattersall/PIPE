package pipe.actions.gui;

import pipe.utilities.gui.GuiUtils;

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
        GuiUtils.displayErrorMessage(null, "Zooming in/out is currently not supported in this version.\n Please file an issue if it is particularly important to you.");
//        if (zoomManager.canZoomIn()) {
//            zoomManager.zoomIn();
//        }
    }
}
