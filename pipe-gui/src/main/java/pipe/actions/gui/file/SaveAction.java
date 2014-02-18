package pipe.actions.gui.file;

import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SaveAction extends AbstractSaveAction {

    public SaveAction(PipeApplicationView pipeApplicationView, PipeApplicationController pipeApplicationController,
                      FileDialog fileChooser) {
        super("Save", "Save", KeyEvent.VK_S, InputEvent.META_DOWN_MASK, pipeApplicationView, pipeApplicationController,
                fileChooser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        saveOperation();
    }

    @Override
    protected boolean doSaveAs() {
        return false;
    }
}
