package pipe.actions.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PipeResourceLocator;
import pipe.utilities.gui.GuiUtils;
import pipe.utilities.io.JarUtilities;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.exceptions.IncludeException;
import uk.ac.imperial.pipe.io.PetriNetFileException;
import uk.ac.imperial.pipe.parsers.UnparsableException;

/**
 * Class responsible for loading the specified example file in the PIPE petri net example menu
 */
@SuppressWarnings("serial")
public class ExampleFileAction extends GuiAction {
    /**
     * File name of the example file
     */
    private String filename;

    /**
     * Parent of this action
     */
    private final Frame parent;

    /**
     * PIPE main application controller
     */
    private final PipeApplicationController applicationController;
    /**
     * Example file
     */
	private File file;

    /**
     *
     * @param file example PNML file
     * @param parent parent of this frame
     * @param applicationController PIPE main application controller
     */
    public ExampleFileAction(File file, Frame parent, PipeApplicationController applicationController) {
        super(file.getName(), "Open example file \"" + file.getName() + "\"");
        this.file = file;
        this.parent = parent;
        this.applicationController = applicationController;
		PipeResourceLocator locator = new PipeResourceLocator(); 
		putValue(SMALL_ICON, new ImageIcon(locator.getImage("Net")));
    }

    /**
     * Extracts files from the jar and loads them to directory "examples" in 
     * the current working directory.  Files are refreshed at each startup of PIPE
     * @param filename of example PNML file from jar
     * @param prefixedName name of the example PNML file as classpath resource
     * @param parent parent of this frame
     * @param controller PIPE main application controller
     */
    public ExampleFileAction(String filename, String prefixedName,
			Frame parent, PipeApplicationController controller) {
    	super(filename, "Open example file \"" + filename + "\""); 
    	this.applicationController = controller;
    	this.parent = parent;
    	this.filename = filename; 
    	InputStream input = getClass().getResourceAsStream(prefixedName);
    	File dir = new File("examples"); 
    	if (!dir.exists()) dir.mkdir();
    	file = new File(dir+File.separator+filename); 
    	try {
			Files.copy(input, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		} 
    	
	}

	/**
     * When performed this action creates a new tab from the specified example Petri net file
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            applicationController.createNewTabFromFile(file);
            System.out.println("Temporary copy of example file opened: "+file.getAbsolutePath());
        } catch (Exception e1) {
            GuiUtils.displayErrorMessage(parent, e1.getMessage());
        }
    }

}
