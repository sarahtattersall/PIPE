package pipe.actions.gui.create;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * This action is responsible for the actions that lead to creating items on the
 * Petri net
 */
public abstract class CreateAction extends GuiAction {

    private final PipeApplicationModel applicationModel;

    public CreateAction(String name, String tooltip, int key, int modifiers, PipeApplicationModel applicationModel) {
        super(name, tooltip, key, modifiers);
        this.applicationModel = applicationModel;
    }

    //TODO: Eventually a handler can tell the GUI Action to do its thing
    // for each type of type clicked on.

    /**
     * Action that happens when a mouse press event is fired on the petri net whilst
     * this action is selected
     *
     * @param event              mouse event
     * @param petriNetController controller for the petri net
     */
    public abstract void doAction(MouseEvent event, PetriNetController petriNetController);

    /**
     * Action that happens when a mouse press event is fired on a {@link pipe.views.ConnectableView}
     * whilst this action is selected
     *
     * @param connectable        item clicked
     * @param petriNetController controller for the petri net
     * @param <T>                connectable type
     */
    public abstract <T extends Connectable> void doConnectableAction(T connectable,
                                                                     PetriNetController petriNetController);

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.selectTypeAction(this);
    }


}
