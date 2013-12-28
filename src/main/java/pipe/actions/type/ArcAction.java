package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.Grid;
import pipe.models.component.Connectable;

import java.awt.*;

public abstract class ArcAction extends TypeAction {

    protected abstract boolean canCreateArcHere(Connectable connectable);
    protected abstract void createArc(Connectable connectable, PetriNetController petriNetController);

    /**
     * Changes the arc end point to the place clicked
     * @param point
     * @param petriNetController
     */
    @Override
    public void doAction(Point point, PetriNetController petriNetController) {
        if (petriNetController.isCurrentlyCreatingArc()) {
            petriNetController.addArcPoint(point.x, point.y, false);
        }
    }

    /**
     *
     * @param connectable
     * @param petriNetController
     */
    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        if (!petriNetController.isCurrentlyCreatingArc() && canCreateArcHere(connectable)) {
            createArc(connectable, petriNetController);
            return;
        }

        if (petriNetController.isCurrentlyCreatingArc()) {
            petriNetController.finishCreatingArc(connectable);
        }
    }


    public ArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }


}

