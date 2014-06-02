package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PIPEConstants;
import pipe.io.JarUtilities;
import pipe.utilities.gui.GuiUtils;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.zip.ZipEntry;

public class ExampleFileAction extends GuiAction
{
    private final File filename;
    private final PipeApplicationView applicationView;

    private final PipeApplicationController applicationController;

    public ExampleFileAction(File file, PipeApplicationView applicationView, PipeApplicationController applicationController)
    {
        super(file.getName(), "Open example file \"" + file.getName() + "\"");
        filename = file;
        this.applicationView = applicationView;
        this.applicationController = applicationController;
        putValue(SMALL_ICON, new ImageIcon(this.getClass().getResource(PIPEConstants.IMAGE_PATH + "Net.png")));
    }

    public ExampleFileAction(ZipEntry entry, PipeApplicationView applicationView, PipeApplicationController applicationController)
    {
        super(entry.getName().substring(1 + entry.getName().indexOf(System.getProperty("file.separator"))), "Open example file \"" + entry.getName() + "\"");
        this.applicationView = applicationView;
        filename = JarUtilities.getFile(entry);
        this.applicationController = applicationController;
        putValue(SMALL_ICON, new ImageIcon(this.getClass().getResource(PIPEConstants.IMAGE_PATH + "Net.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {
           applicationController.createNewTabFromFile(
                    filename);
        } catch (UnparsableException e1) {
            GuiUtils.displayErrorMessage(applicationView, e1.getMessage());
        }
    }

}
