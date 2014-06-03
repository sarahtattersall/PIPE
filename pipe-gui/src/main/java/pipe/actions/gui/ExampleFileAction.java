package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PIPEConstants;
import pipe.io.JarUtilities;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.zip.ZipEntry;

public class ExampleFileAction extends GuiAction {
    private final File filename;

    private final Frame parent;

    private final PipeApplicationController applicationController;

    public ExampleFileAction(File file, Frame parent, PipeApplicationController applicationController) {
        super(file.getName(), "Open example file \"" + file.getName() + "\"");
        filename = file;
        this.parent = parent;
        this.applicationController = applicationController;
        putValue(SMALL_ICON, new ImageIcon(this.getClass().getResource(PIPEConstants.IMAGE_PATH + "Net.png")));
    }

    public ExampleFileAction(ZipEntry entry, Frame parent, PipeApplicationController applicationController) {
        super(entry.getName().substring(1 + entry.getName().indexOf(System.getProperty("file.separator"))),
                "Open example file \"" + entry.getName() + "\"");
        this.parent = parent;
        filename = JarUtilities.getFile(entry);
        this.applicationController = applicationController;
        putValue(SMALL_ICON, new ImageIcon(this.getClass().getResource(PIPEConstants.IMAGE_PATH + "Net.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            applicationController.createNewTabFromFile(filename);
        } catch (UnparsableException e1) {
            GuiUtils.displayErrorMessage(parent, e1.getMessage());
        }
    }

}
