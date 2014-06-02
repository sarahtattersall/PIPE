package pipe.actions.gui.zoom;

import pipe.actions.gui.GuiAction;
import pipe.utilities.gui.GuiUtils;

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
        GuiUtils.displayErrorMessage(null,
                "Zooming in/out is currently not supported in this version.\n Please file an issue if it is particularly important to you.");

//        if (zoomManager.canZoomOut()) {
//            zoomManager.zoomOut();
//        }
    }
}
