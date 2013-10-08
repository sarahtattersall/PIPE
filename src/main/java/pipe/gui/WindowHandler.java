package pipe.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class WindowHandler extends WindowAdapter
{

    public WindowHandler()
    {
    }

    // Handler for window closing event
    public void windowClosing(WindowEvent e)
    {
        ApplicationSettings.getApplicationModel().exitAction.actionPerformed(null);
    }
}
