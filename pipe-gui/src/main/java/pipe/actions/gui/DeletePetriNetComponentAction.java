/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.exceptions.PetriNetComponentException;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.MultipleEdit;
import pipe.models.component.PetriNetComponent;
import pipe.utilities.gui.GuiUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class DeletePetriNetComponentAction extends GuiAction {

    private final PetriNetComponent component;


    public DeletePetriNetComponentAction(PetriNetComponent component) {
        super("Delete Petri net component", "Delete this component");
        this.component = component;
    }

    /**
     * Deletes component from the petri net
     *
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();
        try {
            registerUndoEvent(petriNetController.delete(component));
        } catch (PetriNetComponentException e) {
            GuiUtils.displayErrorMessage(null, e.getMessage());
        }
    }

}
