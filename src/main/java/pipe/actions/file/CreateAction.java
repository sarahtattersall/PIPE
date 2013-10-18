package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class CreateAction extends FileAction {

    public CreateAction() {
        super("New", "Create a new Petri net", "ctrl N");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        view.createNewTab(null, false);
    }
}
