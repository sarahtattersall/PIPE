/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryManager;
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
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();
        HistoryManager historyManager = petriNetController.getHistoryManager();
        historyManager.newEdit(); // new "transaction""
        historyManager.deleteSelection(selected);
        selected.delete();
    }

}
