package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.file.*;
import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class PetriNetEditorManager implements ActionManager {



    private final GuiAction newPetriNetAction;

    private final GuiAction openAction;

    private final GuiAction saveAction;

    private final GuiAction saveAsAction;

    private GuiAction closeAction;

    public PetriNetEditorManager(PipeApplicationView view, PipeApplicationController applicationController) {
        newPetriNetAction = new NewPetriNetAction(applicationController);
        closeAction = new CloseWindowAction(view, applicationController);

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
        saveAction = new SaveAction(view, applicationController, fileDialog);
        saveAsAction = new SaveAsAction(view, applicationController, fileDialog);
        openAction = new OpenAction(applicationController, view, loadFileDialog);
    }

    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(newPetriNetAction, openAction, saveAction, saveAsAction,
                closeAction);

    }

    @Override
    public void enableActions() {
        saveAction.setEnabled(true);
        saveAsAction.setEnabled(true);

    }

    @Override
    public void disableActions() {
        saveAction.setEnabled(false);
        saveAsAction.setEnabled(false);

    }
}
