package pipe.actions.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import pipe.utilities.gui.GuiUtils;

/**
 * Exports the Petri net as a PNG
 */
@SuppressWarnings("serial")
public class ExportPNGAction extends GuiAction {
    /**
     * Constructor
     * Sets short cut to ctrl G
     */
    public ExportPNGAction() {
        super("PNG", "Export the net to PNG format", KeyEvent.VK_G, InputEvent.META_DOWN_MASK);
    }

    /**
     * Saves the Petri net as a PNG when selected.
     *
     * Currently this feature has not been implemented.
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //TODO implement export PNG 
        GuiUtils.displayErrorMessage(null,
                "Export as PNG is currently not supported in this version.\n Please file an issue if it is particularly important to you.");

    }
}
