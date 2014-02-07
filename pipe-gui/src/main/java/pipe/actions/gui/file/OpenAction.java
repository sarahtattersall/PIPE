package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.widgets.FileBrowser;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class OpenAction extends GuiAction {

    private String userPath;
    private File testFile;
    private final PipeApplicationController applicationController;
    private final PipeApplicationView applicationView;

    public OpenAction(PipeApplicationController applicationController, PipeApplicationView applicationView) {
        super("Open", "Open", KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File filePath = getFile();
        if((filePath != null) && filePath.exists() && filePath.isFile() && filePath.canRead())
        {
            userPath = filePath.getParent();
            applicationController.createNewTabFromFile(
                    filePath, applicationView);
        }
        if((filePath != null) && (!filePath.exists()))
        {
            String message = "File \"" + filePath.getName()
                    + "\" does not exist.";
            JOptionPane.showMessageDialog(null, message, "Warning",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *
     * @return File selected from FileBrowser
     */
    private File getFile()
    {
        if (testFile != null)
        {
            return testFile;
        }
        FileBrowser browser = new FileBrowser(userPath);
        return browser.openFile();
    }

    public void setFileForTesting(File fileForTesting) {
        testFile = fileForTesting;
    }
}
