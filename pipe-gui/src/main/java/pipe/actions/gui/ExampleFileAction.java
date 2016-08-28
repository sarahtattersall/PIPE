package pipe.actions.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PipeResourceLocator;
import pipe.utilities.gui.GuiUtils;
import pipe.utilities.io.JarUtilities;
import uk.ac.imperial.pipe.parsers.UnparsableException;

/**
 * Class responsible for loading the specified example file in the PIPE petri net example menu
 */
@SuppressWarnings("serial")
public class ExampleFileAction extends GuiAction {
    /**
     * File location of the example file
     */
    private final File filename;

    /**
     * Parent of this action
     */
    private final Frame parent;

    /**
     * PIPE main application controller
     */
    private final PipeApplicationController applicationController;

    /**
     *
     * @param file example PNML file
     * @param parent parent of this frame
     * @param applicationController PIPE main application controller
     */
    public ExampleFileAction(File file, Frame parent, PipeApplicationController applicationController) {
        super(file.getName(), "Open example file \"" + file.getName() + "\"");
        filename = file;
        this.parent = parent;
        this.applicationController = applicationController;
		PipeResourceLocator locator = new PipeResourceLocator(); 
		putValue(SMALL_ICON, new ImageIcon(locator.getImage("Net")));
    }

    /**
     *
     * @param entry example PNML file stored as a ZipEntry in a jar
     * @param parent parent of this frame
     * @param applicationController PIPE main application controller
     */
    public ExampleFileAction(ZipEntry entry, Frame parent, PipeApplicationController applicationController) {
        super(entry.getName().substring(1 + entry.getName().indexOf(System.getProperty("file.separator"))),
                "Open example file \"" + entry.getName() + "\"");
        this.parent = parent;
        filename = JarUtilities.getFile(entry);
        this.applicationController = applicationController;
		PipeResourceLocator locator = new PipeResourceLocator(); 
		putValue(SMALL_ICON, new ImageIcon(locator.getImage("Net")));
    }

    /**
     * When performed this action creates a new tab from the specified example Petri net file
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            applicationController.createNewTabFromFile(filename);
        } catch (UnparsableException e1) {
            GuiUtils.displayErrorMessage(parent, e1.getMessage());
        }
    }

}
