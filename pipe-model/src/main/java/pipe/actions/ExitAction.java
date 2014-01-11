package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class ExitAction extends GuiAction {

    PipeApplicationView view;
    public ExitAction(PipeApplicationView view) {
        super("Exit", "Close the program", "ctrl Q");
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (view.checkForSaveAll()) {
            view.dispose();
            System.exit(0);
        }
    }
}
