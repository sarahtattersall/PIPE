package pipe.actions.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class PrintAction extends GuiAction {
    public PrintAction() {
        super("Print", "Print", KeyEvent.VK_P, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO
    }
}
