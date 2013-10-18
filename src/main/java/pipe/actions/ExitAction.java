package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class ExitAction extends GuiAction {

    public ExitAction() {
        super("Exit", "Close the program", "ctrl Q");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        view.dispose();
        System.exit(0);
    }
}
