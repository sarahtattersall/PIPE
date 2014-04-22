package pipe.actions.gui.grid;

import pipe.actions.gui.GuiAction;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class GridAction extends GuiAction
{
    private final PipeApplicationView applicationView;

    public GridAction(PipeApplicationView applicationView)
    {
        super("Cycle grid", "Change the grid size (alt-G)", KeyEvent.VK_G, InputEvent.ALT_DOWN_MASK);
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        PetriNetTab petriNetTab = applicationView.getCurrentTab();
        Grid grid = petriNetTab.getGrid();
        grid.increment();
        petriNetTab.repaint();
    }

}
