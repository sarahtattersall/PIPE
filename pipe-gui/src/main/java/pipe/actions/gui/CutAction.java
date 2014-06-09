package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.historyActions.MultipleEdit;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Performs the cut action for cut, copy and paste.
 */
public class CutAction extends GuiAction {
    /**
     * Application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Constructor
     * @param applicationController main PIPE application controller
     */
    public CutAction(PipeApplicationController applicationController) {
        super("Cut", "Cut (Ctrl-X)", KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.applicationController = applicationController;
    }

    /**
     * When this event is called it copies and deletes the selected items.
     *
     * It creates an multi undo action for the cut componentds.
     * @param actionEvent
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        if (!petriNetController.getSelectedComponents().isEmpty()) {
            petriNetController.copySelection();
            try {
                registerUndoEvent(new MultipleEdit(petriNetController.deleteSelection()));
            } catch (PetriNetComponentException e) {
                GuiUtils.displayErrorMessage(null, e.getMessage());
            }
        }
    }
}