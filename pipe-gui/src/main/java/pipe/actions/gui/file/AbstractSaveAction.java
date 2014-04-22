package pipe.actions.gui.file;

import org.apache.commons.io.FilenameUtils;
import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.utilities.gui.GuiUtils;
import pipe.views.PipeApplicationView;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract class for save actions
 * Deals with saving of the petri net
 */
public abstract class AbstractSaveAction extends GuiAction {
    protected final PipeApplicationController pipeApplicationController;

    private final PipeApplicationView pipeApplicationView;

    private final FileDialog fileDialog;

    public AbstractSaveAction(String name, String tooltip, int key, int modifiers,
                              PipeApplicationView pipeApplicationView,
                              PipeApplicationController pipeApplicationController, FileDialog fileDialog) {
        super(name, tooltip, key, modifiers);
        this.pipeApplicationView = pipeApplicationView;
        this.pipeApplicationController = pipeApplicationController;
        this.fileDialog = fileDialog;
    }

    //TODO: Move out into save actions
    protected void saveAsOperation() {
        PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();


        boolean saveFunctional = false;
        //        if (getCurrentPetriNetView().hasFunctionalRatesOrWeights()) {
        //        if (false) {
        //            if (JOptionPane.showConfirmDialog(null, "This net has functional rates or weights expressions. \r\n" +
        //                    "Saving these expression will not allow this PNML file compatible with other tools. \r\n" +
        //                    "Press 'yes' to save them anyway. Press 'no' to save their constant values", "Request",
        //                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
        //                saveFunctional = true;
        //            } else {
        //                saveFunctional = false;
        //            }
        //        }
        fileDialog.setVisible(true);
        File[] files = fileDialog.getFiles();
        if (files.length != 0) {
            File file = files[0];
            saveNet(ensureExtension(file));
        }
    }

    /**
     * Append XML extension onto file if it is missing
     * @param file
     * @return file with extension
     */
    private File ensureExtension(File file) {
        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
        if (ext.isEmpty()) {
            return new File(file.getAbsolutePath() + ".xml");
        }
        return file;
    }

    /**
     * Saves the petri net out to file
     *
     * @param path location of file to save to
     */
    protected void saveNet(String path) {
        saveNet(new File(path));
    }

    /**
     * Saves the petri net out to file
     *
     * @param file file path to save petri net to
     */
    protected void saveNet(File file) {
        try {
            pipeApplicationController.saveAsCurrentPetriNet(file);
        } catch (ParserConfigurationException | TransformerException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            GuiUtils.displayErrorMessage(null, e.getMessage());
        }
    }
}
