package pipe.actions.gui;

import pipe.actions.gui.GuiAction;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ExportTNAction extends GuiAction {
    public ExportTNAction() {
        super("eDSPN", "Export the net to Timenet format", KeyEvent.VK_E, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO:
    }

}
