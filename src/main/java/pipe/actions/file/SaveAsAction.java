package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class SaveAsAction extends FileAction {

    public SaveAsAction() {
        super("Save as", "Save as...", "shift ctrl S");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        view.saveOperation(true);
    }
}
