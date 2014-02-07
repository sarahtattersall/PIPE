package pipe.actions.gui.window;

import pipe.actions.gui.GuiAction;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExitAction extends GuiAction {

    PipeApplicationView view;

    public ExitAction(PipeApplicationView view) {
        super("Exit", "Close the program", KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (view.checkForSaveAll()) {
            view.dispose();
            System.exit(0);
        }
    }
}
