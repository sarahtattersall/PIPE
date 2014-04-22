/*
 * EditNoteAction.java
 */
package pipe.actions;

import pipe.views.viewComponents.Note;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class EditNoteAction
        extends AbstractAction {

   private final Note selected;
   

   public EditNoteAction(Note component) {
      selected = component;
   }

   
   /** Action for editing the text in a Note */
   @Override
   public void actionPerformed(ActionEvent e) {
      selected.enableEditMode();
   }

}
