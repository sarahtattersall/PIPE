package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class PrintAction extends FileAction {
    public PrintAction() {
        super("Print", "Print", "ctrl P");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
        Export.exportGuiView(tab, Export.PRINTER, null);
    }
}
