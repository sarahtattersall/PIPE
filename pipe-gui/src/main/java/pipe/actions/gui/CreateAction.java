package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * This action is responsible for the actions that lead to creating components on the
 * Petri net canvas
 */
public abstract class CreateAction extends GuiAction {

    /**
     * Application model
     */
    private final PipeApplicationModel applicationModel;

    /**
     * Create action constructor
     * @param name name of action
     * @param tooltip string displayed when the user hovers over the corresponding action button on the toolbar.
     * @param key keyboard short cut
     * @param modifiers keyboard short cut modifiers
     * @param applicationModel overall PIPE application model
     */
    public CreateAction(String name, String tooltip, int key, int modifiers, PipeApplicationModel applicationModel) {
        super(name, tooltip, key, modifiers);
        this.applicationModel = applicationModel;
    }

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

    /**
     * When this action is clicked on the tool bar it sets the subclass to be the currently
     * selected action in the application model.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        applicationModel.selectTypeAction(this);
    }


}
