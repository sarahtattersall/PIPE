package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

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
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
        Export.exportGuiView(tab, Export.PNG, null);
    }
}
