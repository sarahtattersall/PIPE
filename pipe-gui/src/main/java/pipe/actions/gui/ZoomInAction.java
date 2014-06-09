package pipe.actions.gui;

import pipe.utilities.gui.GuiUtils;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Perform a zoom in on the Petri net canvas
 */
public class ZoomInAction extends GuiAction {


    /**
     *
     * @param zoomManager Pipe zoom manager
     */
    public ZoomInAction(ZoomManager zoomManager) {
        super("Zoom in", "Zoom in by 10% ", KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    /**
     * Performs the zoom in event
     *
     * This action has currently been disabled due to bugs in the zoom functionality so instead
     * it displays an error message
     * @param actionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        GuiUtils.displayErrorMessage(null,
                "Zooming in/out is currently not supported in this version.\n Please file an issue if it is particularly important to you.");
        //        if (zoomManager.canZoomIn()) {
        //            zoomManager.zoomIn();
        //        }
    }
}
