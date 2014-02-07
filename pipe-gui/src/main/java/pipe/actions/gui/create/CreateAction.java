package pipe.actions.gui.create;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.gui.SelectionManager;
import pipe.gui.model.PipeApplicationModel;
import pipe.models.component.Connectable;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * This action is responsible for the actions that lead to creating items on the
 * Petri net
 */
public abstract class CreateAction extends GuiAction {
    public CreateAction(String name, String tooltip, int key, int modifiers) {
        super(name, tooltip, key, modifiers);
    }

    public CreateAction(String name, String tooltip, String keystroke, boolean toggleable) {
        super(name, tooltip, keystroke, toggleable);
    }

    //TODO: Eventually a handler can tell the GUI Action to do its thing
    // for each type of type clicked on.

    /**
     * Action that happens when a mouse press event is fired on the petri net whilst
     * this action is selected
     * @param event mouse event
     * @param petriNetController controller for the petri net
     */
    public abstract void doAction(MouseEvent event, PetriNetController petriNetController);

    /**
     * Action that happens when a mouse press event is fired on a {@link pipe.views.ConnectableView}
     * whilst this action is selected
     *
     * @param connectable item clicked
     * @param petriNetController controller for the petri net
     * @param <T> connectable type
     */
    public abstract <T extends Connectable> void doConnectableAction(T connectable,
                                                                     PetriNetController petriNetController);

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationModel model = ApplicationSettings.getApplicationModel();
        model.selectTypeAction(this);

        PetriNetTab petriNetTab = ApplicationSettings.getApplicationView().getCurrentTab();
        petriNetTab.setCursorType("crosshair");

        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        SelectionManager selectionManager = controller.getSelectionManager(petriNetTab);
        selectionManager.disableSelection();
    }

}
