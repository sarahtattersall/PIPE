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

/**
 * Opens a PNML Petri net
 */
public class OpenAction extends GuiAction {

    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;


    /**
     * File dialog for selecting a Petri net to load
     */
    private final FileDialog fileChooser;

    /**
     * Constructor
     * @param applicationController Main PIPE application controller
     * @param fileChooser file dialog responsible for choosing files to load
     */
    public OpenAction(PipeApplicationController applicationController, FileDialog fileChooser) {
        super("Open", "Open", KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.fileChooser = fileChooser;
    }

    /**
     * When this action is performed it shows the file dialog and processes the file selected for loading
     * @param e
     */
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
