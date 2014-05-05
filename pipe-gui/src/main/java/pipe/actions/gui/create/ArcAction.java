package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.controllers.arcCreator.ArcActionCreator;
import pipe.exceptions.PetriNetComponentException;
import pipe.gui.PetriNetTab;
import pipe.gui.model.PipeApplicationModel;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;
import pipe.models.petrinet.PetriNet;
import pipe.views.arc.ArcHead;
import pipe.views.arc.TemporaryArcView;
import pipe.visitor.connectable.arc.ArcSourceVisitor;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

public class ArcAction extends CreateAction {

    private final ArcSourceVisitor sourceVisitor;

    private final ArcActionCreator arcCreator;

    private final PipeApplicationController controller;


    private final ArcHead arcHead;

    private TemporaryArcView<? extends Connectable> temporaryArcView = null;

    public ArcAction(String name, String tooltip, int key, int modifiers, ArcSourceVisitor sourceVisitor,
                     ArcActionCreator arcCreator, PipeApplicationModel applicationModel,
                     PipeApplicationController controller, ArcHead arcHead) {
        super(name, tooltip, key, modifiers, applicationModel);
        this.sourceVisitor = sourceVisitor;
        this.arcCreator = arcCreator;
        this.controller = controller;
        this.arcHead = arcHead;
    }

    /**
     * Changes the temporary arc's end point
     *
     * @param event              mouse event that has just been fired
     * @param petriNetController current petri net controller for the tab showing
     */
    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (temporaryArcView != null) {
            temporaryArcView.setEnd(event.getPoint());
            if (event.getClickCount() > 0) {
                Point point = event.getPoint();
                temporaryArcView.addIntermediatePoint(new ArcPoint(point, event.isShiftDown()));
            }

            PetriNetTab tab = petriNetController.getPetriNetTab();
            tab.validate();
            tab.repaint();
        }
    }

    /**
     * @param connectable        the item clicked on
     * @param petriNetController the controller for the current petrinet
     */
    @Override
    public <T extends Connectable> void doConnectableAction(T connectable, PetriNetController petriNetController) {
        PetriNetTab tab = petriNetController.getPetriNetTab();
        if (temporaryArcView == null && sourceVisitor.canStart(connectable)) {
            createTemporaryArc(connectable, tab);

        } else if (temporaryArcView != null && canCreateArcHere(connectable)) {
            createArc(connectable, tab);
        }
    }

    /**
     * @param connectable Source of the temporary arc
     * @param tab         Tab to add TemporaryArc to
     * @param <T>         Source class
     * @return created Temporary arc
     */
    private <T extends Connectable> void createTemporaryArc(T connectable, final PetriNetTab tab) {

        temporaryArcView = new TemporaryArcView<>(connectable, arcHead);
        temporaryArcView.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_META:
                    case KeyEvent.VK_WINDOWS:
                    case KeyEvent.VK_SPACE:
                        tab.setMetaDown(true);
                        break;

                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_DELETE:
                        tab.remove(temporaryArcView);
                        tab.repaint();
                        temporaryArcView = null;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        tab.add(temporaryArcView);
        temporaryArcView.requestFocusInWindow();
    }

    private <T extends Connectable> boolean canCreateArcHere(T connectable) {
        return arcCreator.canCreate(temporaryArcView.getSourceConnectable(), connectable);
    }

    private <T extends Connectable> void createArc(T connectable, PetriNetTab tab) {
        try {
            ArcCreatorVisitor visitor = new ArcCreatorVisitor();

            temporaryArcView.getSourceConnectable().accept(visitor);
            boolean inbound = visitor.place != null;
            connectable.accept(visitor);
            PetriNetController petriNetController = controller.getActivePetriNetController();
            PetriNet net = petriNetController.getPetriNet();
            Arc<? extends Connectable, ? extends Connectable> arc;
            if (inbound) {
                arc = arcCreator.createInboundArc(visitor.place, visitor.transition,
                        temporaryArcView.getIntermediatePoints());

            } else {
                arc = arcCreator.createOutboundArc(visitor.place, visitor.transition,
                        temporaryArcView.getIntermediatePoints());
            }

            registerUndoEvent(new AddPetriNetObject(arc, net));
            net.add(arc);
        } catch (PetriNetComponentException e) {
            //TODO:
            e.printStackTrace();
        }

        tab.remove(temporaryArcView);
        tab.repaint();
        temporaryArcView = null;
    }

    /**
     * Sets place and transition properties accordingly
     */
    private class ArcCreatorVisitor implements PlaceVisitor, TransitionVisitor {

        Place place = null;

        Transition transition = null;

        @Override
        public void visit(Place place) {
            this.place = place;
        }

        @Override
        public void visit(Transition transition) {
            this.transition = transition;
        }
    }


}

