package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;
import pipe.models.visitor.connectable.arc.ArcCreatorVisitor;
import pipe.models.visitor.connectable.arc.ArcSourceVisitor;
import pipe.views.TemporaryArcView;

import java.awt.event.MouseEvent;

public class ArcAction extends TypeAction {

    private final ArcSourceVisitor sourceVisitor;
    private final ArcCreatorVisitor creatorVisitor;
//    private final TemporaryArcView<?> temporaryArcView;

    public ArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke, ArcSourceVisitor sourceVisitor, ArcCreatorVisitor creatorVisitor) {
        super(name, typeID, tooltip, keystroke);
        this.sourceVisitor = sourceVisitor;
        this.creatorVisitor = creatorVisitor;
    }

    /**
     * Adds an intermediate point to the point clicked
     * @param event
     * @param petriNetController
     */
    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (petriNetController.isCurrentlyCreatingArc()) {
            petriNetController.addPoint(event.getPoint(), event.isShiftDown());
        }
    }

    /**
     *
     * @param connectable the item clicked on
     * @param petriNetController the controller for the current petrinet
     */
    @Override
    public <S extends Connectable<T, S>, T extends Connectable<S, T>> void doConnectableAction(Connectable<S, T> connectable, PetriNetController petriNetController) {
//        if (temporaryArcView == null) {
//            temporaryArcView = new TemporaryArcView<?>(connectable);
//        }
        if (!petriNetController.isCurrentlyCreatingArc() && canCreateArcHere(connectable)) {
            createArc(connectable);
            return;
        }

        if (petriNetController.isCurrentlyCreatingArc()) {
            petriNetController.finishCreatingArc(connectable);
        }
    }




    private <S extends Connectable<T, S>, T extends Connectable<S, T>> boolean canCreateArcHere(Connectable<S, T> connectable) {
          return sourceVisitor.canCreate(connectable);
    }

    private <S extends Connectable<T, S>, T extends Connectable<S, T>> void createArc(Connectable<S, T> connectable) {
        connectable.accept(creatorVisitor);
    }



}

