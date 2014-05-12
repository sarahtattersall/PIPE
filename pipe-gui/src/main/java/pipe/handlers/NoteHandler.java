/*
 * Created on 
 * Author is 
 *
 */
package pipe.handlers;


import pipe.controllers.PetriNetController;
import pipe.views.viewComponents.AnnotationView;
import uk.ac.imperial.pipe.models.component.annotation.Annotation;

import java.awt.Container;

public class NoteHandler
        extends PetriNetObjectHandler<Annotation, AnnotationView>
{
   
   
   NoteHandler(AnnotationView view, Container contentpane, Annotation note, PetriNetController controller) {
      super(view, contentpane, note, controller);
      enablePopup = true;
   }

}
