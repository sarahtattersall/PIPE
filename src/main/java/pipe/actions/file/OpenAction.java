package pipe.actions.file;

import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.FileBrowser;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class OpenAction extends FileAction {

    private String userPath;
    private File testFile;

    public OpenAction() {
        super("Open", "Open", "ctrl O");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File filePath = getFile();
        if((filePath != null) && filePath.exists() && filePath.isFile() && filePath.canRead())
        {
            userPath = filePath.getParent();
            PipeApplicationView view = ApplicationSettings.getApplicationView();
            ApplicationSettings.getApplicationController().createNewTabFromFile(
                    filePath, false);
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
