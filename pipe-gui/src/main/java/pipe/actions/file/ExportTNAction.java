package pipe.actions.file;

import pipe.actions.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ExportTNAction extends GuiAction {
    public ExportTNAction() {
        super("eDSPN", "Export the net to Timenet format", KeyEvent.VK_E, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
        Export.exportGuiView(tab, Export.TN, null);
    }

}
