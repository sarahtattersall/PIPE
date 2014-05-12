package pipe.actions.gui.file;

import pipe.controllers.PipeApplicationController;
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

public class SaveAction extends AbstractSaveAction {

    public SaveAction(PipeApplicationController pipeApplicationController, FileDialog fileChooser) {
        super("Save", "Save", KeyEvent.VK_S, InputEvent.META_DOWN_MASK, pipeApplicationController,
                fileChooser);
    }

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


    protected boolean doSaveAs() {
        PetriNet petriNet = pipeApplicationController.getActivePetriNetController().getPetriNet();
        SaveAsVisitor visitor = new SaveAsVisitor();
        return visitor.shouldSaveAs(petriNet);
    }

    /**
     * Visits PetriNetName possible classes and detemrines if the save action
     * should be a save as action
     */
    private class FileNamer implements NormalNameVisitor, FileNameVisitor {

        private File file = new File("");

        /**
         * @param petriNet
         * @return file location and name to save petri net to
         */
        public File getFile(PetriNet petriNet) {
            petriNet.getName().visit(this);
            return file;
        }

        @Override
        public void visit(PetriNetFileName name) {
            file = name.getFile();
        }

        @Override
        public void visit(NormalPetriNetName name) {
        }
    }


    /**
     * Visits PetriNetName possible classes and detemrines if the save action
     * should be a save as action
     */
    private class SaveAsVisitor implements NormalNameVisitor, FileNameVisitor {

        private boolean saveAs = false;

        /**
         * Determines if the petri net needs a save as call by
         * visiting the name item
         * @param petriNet
         * @return if a save as should be performed
         */
        public boolean shouldSaveAs(PetriNet petriNet) {
            petriNet.getName().visit(this);
            return saveAs;
        }

        @Override
        public void visit(PetriNetFileName name) {
            saveAs = false;
        }

        /**
         * If the name is a normal name it does not yet have a file representation
         * and so a save as is needed
         * @param name
         */
        @Override
        public void visit(NormalPetriNetName name) {
            saveAs = true;
        }
    }
}
