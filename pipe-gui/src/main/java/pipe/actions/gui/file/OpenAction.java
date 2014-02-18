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

    private String userPath;

    private File testFile;

    public OpenAction(PipeApplicationController applicationController, PipeApplicationView applicationView,
                      FileDialog fileChooser) {
        super("Open", "Open", KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.applicationView = applicationView;
        this.fileChooser = fileChooser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String path = getFile();
        if (path != null) {
            File file = new File(fileChooser.getDirectory(), path);
            if (file.exists() && file.isFile() && file.canRead()) {
                userPath = file.getParent();
                applicationController.createNewTabFromFile(file, applicationView);
            } else {
                String message = "File \"" + file.getName() + "\" does not exist.";
                JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * @return File selected from FileBrowser
     */
    private String getFile() {
        if (testFile != null) {
            return testFile.getAbsolutePath();
        }
        fileChooser.setVisible(true);
        return fileChooser.getFile();
    }

    public void setFileForTesting(File fileForTesting) {
        testFile = fileForTesting;
    }
}
