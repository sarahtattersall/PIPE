package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class OpenAction extends GuiAction {

    private final PipeApplicationController applicationController;

    private final PipeApplicationView applicationView;

    private final FileDialog fileChooser;

    public OpenAction(PipeApplicationController applicationController, PipeApplicationView applicationView,
                      FileDialog fileChooser) {
        super("Open", "Open", KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.applicationView = applicationView;
        this.fileChooser = fileChooser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fileChooser.setVisible(true);
        for (File file : fileChooser.getFiles()) {
            if (file.exists() && file.isFile() && file.canRead()) {
                applicationController.createNewTabFromFile(file, applicationView);
            } else {
                String message = "File \"" + file.getName() + "\" does not exist.";
                JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
