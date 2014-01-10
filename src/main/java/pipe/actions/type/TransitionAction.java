package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.Grid;
import pipe.historyActions.AddPetriNetObject;
import pipe.models.PetriNet;
import pipe.models.component.Connectable;
import pipe.models.component.Transition;
import pipe.views.PipeApplicationView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;
import pipe.views.builder.PlaceViewBuilder;
import pipe.views.builder.TransitionViewBuilder;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Abstract class to created timed/untimed transactions
 */
public abstract class TransitionAction extends TypeAction {

    public TransitionAction(final String name, final int typeID,
                            final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Transition transition = newTransition(event.getPoint(), petriNetController);
            PetriNet net = petriNetController.getPetriNet();
            petriNetController.getHistoryManager().addNewEdit(new AddPetriNetObject(transition, net));
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
        transition.setX((double) Grid.getModifiedX(point.x));
        transition.setY((double) Grid.getModifiedY(point.y));
        transition.setTimed(isTimed());

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addTransition(transition);


        return transition;
    }

    private String getNetTransitionName(PetriNetController petriNetController) {
        int number = petriNetController.getUniqueTransitionNumber();
        String id = "T" + number;
        return id;
    }

    protected abstract boolean isTimed();

}
