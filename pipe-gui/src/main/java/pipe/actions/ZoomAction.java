package pipe.actions;

import pipe.actions.gui.GuiAction;
import pipe.utilities.gui.GuiUtils;

import java.awt.event.ActionEvent;

/**
 * Zoom action for zooming in and out of the canvas
 */
public class ZoomAction extends GuiAction
{
    /**
     * Constructor
     * @param name
     * @param tooltip
     * @param keystroke
     */
    public ZoomAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    /**
     * Performs the zoom
     *
     * This action is currently unsupported due to bugs in the zoom functionality. Instead an error
     * message is displayed
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        GuiUtils.displayErrorMessage(null,
                "Zooming in/out is currently not supported in this version.\n Please file an issue if it is particularly important to you.");

    }


}
