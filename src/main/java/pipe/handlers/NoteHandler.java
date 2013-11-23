/*
 * Created on 
 * Author is 
 *
 */
package pipe.handlers;

import java.awt.Container;
import java.awt.event.MouseEvent;

import pipe.controllers.PetriNetController;
import pipe.models.Annotation;
import pipe.views.MarkingView;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.Note;


public class NoteHandler 
        extends PetriNetObjectHandler<Annotation, AnnotationNote>
{
   
   
   NoteHandler(AnnotationNote view, Container contentpane, Annotation note, PetriNetController controller) {
      super(view, contentpane, note, controller);
      enablePopup = true;
   }

   
   public void mousePressed(MouseEvent e) {      
//      if ((e.getComponent() == component) || !e.getComponent().isEnabled()){
//         super.mousePressed(e);
//      }
   }

   
   public void mouseDragged(MouseEvent e) {
//      if ((e.getComponent() == component) || !e.getComponent().isEnabled()){
//         super.mouseDragged(e);
//      }
   }

   
   public void mouseReleased(MouseEvent e) {
//      if ((e.getComponent() == component) || !e.getComponent().isEnabled()){
//         super.mouseReleased(e);
//      }
   }
   
}
