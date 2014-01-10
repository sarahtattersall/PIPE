package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class ExportPSAction extends FileAction {
    public ExportPSAction() {
        super("PostScript", "Export the net to PostScript format", "ctrl T");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
//        Export.exportGuiView(tab, Export.POSTSCRIPT, view.getCurrentPetriNetView());
    }

}
