package pipe.actions;

import pipe.actions.gui.GuiAction;
import pipe.utilities.gui.GuiUtils;

import java.awt.event.ActionEvent;

public class ZoomAction extends GuiAction
{
    public ZoomAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        GuiUtils.displayErrorMessage(null,
                "Zooming in/out is currently not supported in this version.\n Please file an issue if it is particularly important to you.");

    }


}
