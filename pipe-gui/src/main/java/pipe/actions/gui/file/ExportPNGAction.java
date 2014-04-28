package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ExportPNGAction extends GuiAction {
    /**
     * Sets short cut to ctrl G
     */
    public ExportPNGAction() {
        super("PNG", "Export the net to PNG format", KeyEvent.VK_G, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO
    }
}
