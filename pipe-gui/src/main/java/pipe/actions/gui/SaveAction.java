package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.FileNameVisitor;
import uk.ac.imperial.pipe.models.petrinet.name.NormalNameVisitor;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetFileName;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Responsible for performing a save action on the Petri net.
 *
 * This save action will use the Petri nets underlying file if it exists, or perform a save as action
 */
@SuppressWarnings("serial")
public class SaveAction extends AbstractSaveAction {

    /**
     * Constructor
     * @param pipeApplicationController PIPE main application controller
     * @param fileChooser save file dialog chooser
     */
    public SaveAction(PipeApplicationController pipeApplicationController, FileDialog fileChooser) {
        super("Save", "Save", KeyEvent.VK_S, InputEvent.META_DOWN_MASK, pipeApplicationController, fileChooser);
    }

    /**
     * Tries to perform a save action, if the Petri net does not yet have an underlying file associated
     * with it, a save as will be performed.
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (doSaveAs()) {
            saveAsOperation();
        } else {
            PetriNet petriNet = pipeApplicationController.getActivePetriNetController().getPetriNet();
            FileNamer fileNamer = new FileNamer();
            saveNet(fileNamer.getFile(petriNet));
        }
    }


    /**
     *
     * @return true if the Petri net does not yet have an underlying file associated with it. This indicates that
     * a save as should be performed.
     */
    protected boolean doSaveAs() {
        PetriNet petriNet = pipeApplicationController.getActivePetriNetController().getPetriNet();
        SaveAsVisitor visitor = new SaveAsVisitor();
        return visitor.shouldSaveAs(petriNet);
    }

    /**
     * Visits PetriNetName possible classes and determines if the save action
     * should be a save as action
     */
    private static class FileNamer implements NormalNameVisitor, FileNameVisitor {

        /**
         * Petri net file
         */
        private File file = new File("");

        /**
         * @param petriNet to be saved  
         * @return file location and name to save petri net to
         */
        public File getFile(PetriNet petriNet) {
            petriNet.getName().visit(this);
            return file;
        }

        /**
         * sets the file to the existing Petri net file
         * @param name of the file 
         */
        @Override
        public void visit(PetriNetFileName name) {
            file = name.getFile();
        }

        /**
         * Noop operation
         * @param name of the file 
         */
        @Override
        public void visit(NormalPetriNetName name){
            // No action needed
        }
    }


    /**
     * Visits PetriNetName possible classes and determines if the save action
     * should be a save as action
     */
    private static class SaveAsVisitor implements NormalNameVisitor, FileNameVisitor {

        /**
         * Determines if a save as should be performed
         */
        private boolean saveAs = false;

        /**
         * Determines if the petri net needs a save as call by
         * visiting the name item
         *
         * @param petriNet to be saved 
         * @return if a save as should be performed
         */
        public boolean shouldSaveAs(PetriNet petriNet) {
            petriNet.getName().visit(this);
            return saveAs;
        }

        /**
         * If the Petri net has an existing file then saveAs is set to false
         * @param name of the file 
         */
        @Override
        public void visit(PetriNetFileName name) {
            saveAs = false;
        }

        /**
         * If the name is a normal name it does not yet have a file representation
         * and so a save as is needed
         *
         * @param name of the file
         */
        @Override
        public void visit(NormalPetriNetName name) {
            saveAs = true;
        }
    }
}
