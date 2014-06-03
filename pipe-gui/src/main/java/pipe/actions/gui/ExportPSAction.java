package pipe.actions.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ExportPSAction extends GuiAction {
    /**
     * Sets shortcut to ctrl T
     */
    public ExportPSAction() {
        super("PostScript", "Export the net to PostScript format", KeyEvent.VK_T, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //        Export.exportGuiView(tab, Export.POSTSCRIPT, view.getCurrentPetriNetView());
    }

}
