package pipe.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.views.viewComponents.AnnotationNote;


/**
 * Action to toggle the background of a note between white and transparent
 * @author Tim Kimber
 */
public class EditAnnotationBackgroundAction 
        extends AbstractAction {

   private final AnnotationNote note;
   
   
   public EditAnnotationBackgroundAction(AnnotationNote an) {
      note = an;
   }
   
   
   public void actionPerformed(ActionEvent e) {
      note.changeBackground();
      note.repaint();
   }

}
