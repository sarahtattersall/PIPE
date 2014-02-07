package pipe.actions.file;

import pipe.actions.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SaveAction extends GuiAction {

    public SaveAction() {
        super("Save", "Save", KeyEvent.VK_S, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        view.saveOperation(true);
    }
}
