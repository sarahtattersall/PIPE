/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.views.PetriNetViewComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class DeletePetriNetObjectAction extends AbstractAction {

   private final PetriNetViewComponent selected;

   
   public DeletePetriNetObjectAction(PetriNetViewComponent component) {
      selected = component;
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent e) {
       ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().newEdit(); // new "transaction""
       ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().deleteSelection(selected);
      selected.delete();
   }

}
