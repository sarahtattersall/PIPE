package pipe.actions.grid;

import pipe.actions.GuiAction;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

//TODO IS THIS EVEN A TYPE ACTION?
public class DragAction extends GuiAction {
    private final PipeApplicationView pipeApplicationView;

    public DragAction(PipeApplicationView pipeApplicationView) {
        super("Drag", "Drag the drawing alt-D", KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationView = pipeApplicationView;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNetTab tab = pipeApplicationView.getCurrentTab();
        tab.setCursorType("move");
    }
}
