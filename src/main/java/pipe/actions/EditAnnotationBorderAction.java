/*
 * Created on 07-Mar-2004
 * Author is Michael Camacho
 */
package pipe.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.gui.ApplicationSettings;
import pipe.views.viewComponents.AnnotationNote;


public class EditAnnotationBorderAction
        extends AbstractAction {

   private final AnnotationNote selected;
   

   public EditAnnotationBorderAction(AnnotationNote component) {
      selected = component;
   }

      
   /** Action for editing the text in an AnnotationNote */
   public void actionPerformed(ActionEvent e) {
       ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
               selected.showBorder(!selected.isShowingBorder()));
   }

}
