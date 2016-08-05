package pipe.actions.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import pipe.utilities.gui.GuiUtils;

/**
 * Exports the Petri net in time net format
 */
@SuppressWarnings("serial")
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
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO: implement export in time net format 
        GuiUtils.displayErrorMessage(null,
                "Export in time net format is currently not supported in this version.\n Please file an issue if it is particularly important to you.");

    }

}
