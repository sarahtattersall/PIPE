/*
 * Created on 
 * Author is 
 *
 */
package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.models.component.annotation.Annotation;
import pipe.views.viewComponents.AnnotationView;

import java.awt.*;
import java.awt.event.MouseEvent;

public class NoteHandler
        extends PetriNetObjectHandler<Annotation, AnnotationView>
{
   
   
   NoteHandler(AnnotationView view, Container contentpane, Annotation note, PetriNetController controller) {
      super(view, contentpane, note, controller);
      enablePopup = true;
   }

}
