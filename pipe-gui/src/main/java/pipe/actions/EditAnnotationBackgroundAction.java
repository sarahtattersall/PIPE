package pipe.actions;

import pipe.views.AnnotationView;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Action to toggle the background of a note between white and transparent
 * @author Tim Kimber
 */
public class EditAnnotationBackgroundAction 
        extends AbstractAction {

   private final AnnotationView note;
   
   
   public EditAnnotationBackgroundAction(AnnotationView an) {
      note = an;
   }
   
   
   public void actionPerformed(ActionEvent e) {
      note.changeBackground();
      note.repaint();
   }

}
