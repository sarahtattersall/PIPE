package pipe.actions.file;

import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class CreateAction extends FileAction {


    private final PipeApplicationView applicationView;

    public CreateAction(PipeApplicationView applicationView) {
        super("New", "Create a new Petri net", "ctrl N");
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        controller.createEmptyPetriNet(applicationView);
    }
}
