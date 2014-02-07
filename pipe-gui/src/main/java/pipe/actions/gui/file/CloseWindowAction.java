package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CloseWindowAction extends GuiAction {
    private final PipeApplicationView pipeApplicationView;

    public CloseWindowAction(PipeApplicationView pipeApplicationView) {
        super("Close", "Close the current tab", KeyEvent.VK_W, InputEvent.META_DOWN_MASK);
        this.pipeApplicationView = pipeApplicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pipeApplicationView.removeCurrentTab();
    }
}
