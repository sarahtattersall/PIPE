package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class CreateAction extends FileAction {

    public CreateAction() {
        super("New", "Create a new Petri net", "ctrl N");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        ApplicationSettings.getApplicationController().createNewTab(null, false);
    }
}
