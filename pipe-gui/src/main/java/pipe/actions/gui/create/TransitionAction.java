package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.Connectable;
import pipe.models.component.transition.Transition;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Abstract class to created timed/untimed transactions
 */
public abstract class TransitionAction extends CreateAction {

    public TransitionAction(String name, String tooltip, int key, int modifiers, PipeApplicationModel applicationModel) {
        super(name,  tooltip, key, modifiers, applicationModel);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = event.getPoint();
            Transition transition = newTransition(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();

            registerUndoEvent(new AddPetriNetObject(transition, net));
        }
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        // Do nothing if clicked on existing connectable
    }

    private Transition newTransition(Point point, PetriNetController petriNetController) {
        //TODO: MOVE THIS OUT TO CONTROLLER, ALSO NEED TO ADD TO PETRINET MODEL...
        String id = getNetTransitionName(petriNetController);
        Transition transition = new Transition(id, id);
        transition.setX(point.x);
        transition.setY(point.y);
        transition.setTimed(isTimed());

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addTransition(transition);


        return transition;
    }

    private String getNetTransitionName(PetriNetController petriNetController) {
        return petriNetController.getUniqueTransitionName();
    }

    protected abstract boolean isTimed();

}
