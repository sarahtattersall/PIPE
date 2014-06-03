package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.*;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class OpenAction extends GuiAction {

    private final PipeApplicationController applicationController;


    private final FileDialog fileChooser;

    public OpenAction(PipeApplicationController applicationController, FileDialog fileChooser) {
        super("Open", "Open", KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.fileChooser = fileChooser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fileChooser.setVisible(true);
        for (File file : fileChooser.getFiles()) {
            if (file.exists() && file.isFile() && file.canRead()) {
                try {
                    applicationController.createNewTabFromFile(file);
                } catch (UnparsableException e1) {
                    GuiUtils.displayErrorMessage(null, e1.getMessage());
                }
            } else {
                String message = "File \"" + file.getName() + "\" does not exist.";
                JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
