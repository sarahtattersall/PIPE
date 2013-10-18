package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class ExportTNAction extends FileAction {
    public ExportTNAction() {
        super("eDSPN", "Export the net to Timenet format", "ctrl E");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
        Export.exportGuiView(tab, Export.TN, null);
    }

}
