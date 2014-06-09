package pipe.actions.manager;

import pipe.actions.gui.*;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Stores the actions for modifying a Petri net
 */
public class PetriNetEditorManager implements ActionManager {


    /**
     * Creates a new Petri net
     */
    private final GuiAction newPetriNetAction;

    /**
     * Loads a Petri net from an XML file
     */
    private final GuiAction openAction;

    /**
     * Saves a Petri net to an XML file
     */
    private final GuiAction saveAction;

    /**
     * Saves a Petri net to a specified XML file
     */
    private final GuiAction saveAsAction;

    /**
     * Closes the Petri net tab
     */
    private GuiAction closeAction;

    /**
     * Constructor
     * @param view main PIPE application view
     * @param applicationController main PIPE application controller
     */
    public PetriNetEditorManager(PipeApplicationView view, PipeApplicationController applicationController) {
        newPetriNetAction = new NewPetriNetAction(applicationController);
        closeAction = new CloseWindowAction(applicationController);

        FileDialog fileDialog = new FileDialog(view, "Save Petri Net", FileDialog.SAVE);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        FileDialog loadFileDialog = new FileDialog(view, "Open Petri Net", FileDialog.LOAD);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        saveAction = new SaveAction(applicationController, fileDialog);
        saveAsAction = new SaveAsAction(applicationController, fileDialog);
        openAction = new OpenAction(applicationController, loadFileDialog);
    }

    /**
     *
     * @return actions housed for creating and saving Petri nets
     */
    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(newPetriNetAction, openAction, saveAction, saveAsAction,
                closeAction);

    }

    /**
     * Enable actions for swapping into edit mode
     */
    @Override
    public void enableActions() {
        saveAction.setEnabled(true);
        saveAsAction.setEnabled(true);

    }

    /**
     * Disable actions for swapping into animation mode
     */
    @Override
    public void disableActions() {
        saveAction.setEnabled(false);
        saveAsAction.setEnabled(false);

    }
}
