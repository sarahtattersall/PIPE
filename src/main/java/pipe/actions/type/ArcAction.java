package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.Grid;
import pipe.models.component.Connectable;
import pipe.models.visitor.connectable.arc.ArcCreatorVisitor;
import pipe.models.visitor.connectable.arc.ArcSourceVisitor;

import java.awt.*;

public class ArcAction extends TypeAction {

    private final ArcSourceVisitor sourceVisitor;
    private final ArcCreatorVisitor creatorVisitor;

    public ArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke, ArcSourceVisitor sourceVisitor, ArcCreatorVisitor creatorVisitor) {
        super(name, typeID, tooltip, keystroke);
        this.sourceVisitor = sourceVisitor;
        this.creatorVisitor = creatorVisitor;
    }

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
            createArc(connectable);
            return;
        }

        if (petriNetController.isCurrentlyCreatingArc()) {
            petriNetController.finishCreatingArc(connectable);
        }
    }




    private boolean canCreateArcHere(Connectable connectable) {
          return sourceVisitor.canCreate(connectable);
    }

    private void createArc(Connectable connectable) {
        connectable.accept(creatorVisitor);
    }



}

