package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class SaveAction extends FileAction {

    public SaveAction() {
        super("Save", "Save", "ctrl S");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        view.saveOperation(true);
    }
}
