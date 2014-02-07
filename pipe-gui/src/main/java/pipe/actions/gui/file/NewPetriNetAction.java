package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class NewPetriNetAction extends GuiAction {


    private final PipeApplicationView applicationView;

    public NewPetriNetAction(PipeApplicationView applicationView) {
        super("New", "Create a new Petri net", KeyEvent.VK_N, InputEvent.META_DOWN_MASK);
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        controller.createEmptyPetriNet(applicationView);
    }
}
