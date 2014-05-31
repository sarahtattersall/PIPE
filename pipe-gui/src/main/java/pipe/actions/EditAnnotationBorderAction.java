/*
 * Created on 07-Mar-2004
 * Author is Michael Camacho
 */
package pipe.actions;

import uk.ac.imperial.pipe.models.petrinet.Annotation;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class EditAnnotationBorderAction extends AbstractAction {

    private final Annotation annotation;


    public EditAnnotationBorderAction(Annotation annotation) {
        this.annotation = annotation;
    }

    /**
     * Action for editing the text in an AnnotationNote
     */
    @Override
    public void actionPerformed(ActionEvent e) {

//        PipeApplicationController controller = ApplicationSettings.getApplicationController();
//        PetriNetController petriNetController = controller.getActivePetriNetController();
          annotation.toggleBorder();
        //TODO: UNDO ACTION!
//        petriNetController.getHistoryManager().addNewEdit(annotation.showBorder(!annotation.isShowingBorder()));
    }

}
