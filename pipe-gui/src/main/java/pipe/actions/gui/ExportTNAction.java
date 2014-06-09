package pipe.actions.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Exports the Petri net in time net format
 */
public class ExportTNAction extends GuiAction {
    /**
     * Constructor
     */
    public ExportTNAction() {
        super("eDSPN", "Export the net to Timenet format", KeyEvent.VK_E, InputEvent.META_DOWN_MASK);
    }

    /**
     * Saves the Petri net in time net format
     *
     * This feature has not yet been implemented
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO:
    }

}
