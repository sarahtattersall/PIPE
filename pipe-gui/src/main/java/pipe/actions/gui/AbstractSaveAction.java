package pipe.actions.gui;

import org.apache.commons.io.FilenameUtils;
import pipe.controllers.application.PipeApplicationController;
import pipe.utilities.gui.GuiUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.FileDialog;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract class for save actions
 * Deals with saving of the petri net
 */
public abstract class AbstractSaveAction extends GuiAction {
    protected final PipeApplicationController pipeApplicationController;

    private final FileDialog fileDialog;

    public AbstractSaveAction(String name, String tooltip, int key, int modifiers,
                              PipeApplicationController pipeApplicationController, FileDialog fileDialog) {
        super(name, tooltip, key, modifiers);
        this.pipeApplicationController = pipeApplicationController;
        this.fileDialog = fileDialog;
    }

    //TODO: Move out into save actions
    protected final void saveAsOperation() {


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
     * Saves the petri net out to file
     *
     * @param file file path to save petri net to
     */
    protected final void saveNet(File file) {
        try {
            pipeApplicationController.saveAsCurrentPetriNet(file);
        } catch (ParserConfigurationException | TransformerException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            GuiUtils.displayErrorMessage(null, e.getMessage());
        }
    }

    /**
     * Append XML extension onto file if it is missing
     *
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
}
