package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.gui.Grid;

import java.awt.event.ActionEvent;

public class GridAction extends GuiAction
{
    public GridAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        Grid.increment();
        ApplicationSettings.getApplicationView().repaint();
    }

}
