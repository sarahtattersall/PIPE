package pipe.actions.gui.file;

import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SaveAsAction extends AbstractSaveAction {

    public SaveAsAction(PipeApplicationView pipeApplicationView, PipeApplicationController pipeApplicationController,
                        FileDialog fileChooser) {
        super("Save as", "Save as...", KeyEvent.VK_S, InputEvent.META_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK,
                pipeApplicationView, pipeApplicationController, fileChooser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        saveAsOperation();
    }
}
