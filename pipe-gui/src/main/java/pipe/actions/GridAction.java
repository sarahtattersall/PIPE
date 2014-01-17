package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class GridAction extends GuiAction
{
    private final PipeApplicationView applicationView;

    public GridAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView)
    {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
    }

    public void actionPerformed(ActionEvent e)
    {
        PetriNetTab petriNetTab = applicationView.getCurrentTab();
        Grid grid = petriNetTab.getGrid();
        grid.increment();
        petriNetTab.repaint();
    }

}
