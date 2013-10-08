/*
 * Created on 
 * Author is 
 *
 */
package pipe.handlers;

import java.awt.Container;
import java.awt.event.MouseEvent;

import pipe.views.viewComponents.Note;


public class NoteHandler 
        extends PetriNetObjectHandler
{
   
   
   NoteHandler(Container contentpane, Note note) {
      super(contentpane, note);
      enablePopup = true;
   }

   
   public void mousePressed(MouseEvent e) {      
      if ((e.getComponent() == my) || !e.getComponent().isEnabled()){
         super.mousePressed(e);
      }
   }

   
   public void mouseDragged(MouseEvent e) {
      if ((e.getComponent() == my) || !e.getComponent().isEnabled()){
         super.mouseDragged(e);
      }
   }

   
   public void mouseReleased(MouseEvent e) {
      if ((e.getComponent() == my) || !e.getComponent().isEnabled()){
         super.mouseReleased(e);
      }
   }
   
}
