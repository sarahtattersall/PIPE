package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.controllers.arcCreator.ArcActionCreator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.models.component.Connectable;
import pipe.models.component.Token;
import pipe.models.visitor.connectable.arc.ArcSourceVisitor;
import pipe.views.PipeApplicationView;
import pipe.views.TemporaryArcView;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ArcAction extends TypeAction {

    private final ArcSourceVisitor sourceVisitor;
    private final ArcActionCreator arcCreator;
    private TemporaryArcView<? extends Connectable> temporaryArcView = null;
    private final PipeApplicationController controller;
    private final PipeApplicationView applicationView;

    public ArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke, ArcSourceVisitor sourceVisitor, ArcActionCreator arcCreator, PipeApplicationController controller, PipeApplicationView applicationView) {
        super(name, typeID, tooltip, keystroke);
        this.sourceVisitor = sourceVisitor;
        this.arcCreator = arcCreator;
        this.controller = controller;
        this.applicationView = applicationView;
    }

    /**
     * Changes the temporary arc's end point
     *
     * @param event mouse event that has just been fired
     * @param petriNetController current petri net controller for the tab showing
     */
    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (temporaryArcView != null) {
            temporaryArcView.setEnd(event.getPoint());
            PetriNetTab tab =  applicationView.getCurrentTab();
            tab.validate();
            tab.repaint();
        }
    }

    /**
     * @param connectable        the item clicked on
     * @param petriNetController the controller for the current petrinet
     */
    @Override
    public <T extends Connectable> void doConnectableAction(T connectable,
                                    PetriNetController petriNetController) {
        if (temporaryArcView == null && sourceVisitor.canStart(connectable)) {
            temporaryArcView = new TemporaryArcView<T>(connectable);
            PetriNetTab tab = applicationView.getCurrentTab();
            tab.add(temporaryArcView);
        }  else if (temporaryArcView != null && canCreateArcHere(connectable)) {
            createArc(connectable);
        }
    }

    private <T extends Connectable> boolean canCreateArcHere(T connectable) {
        return arcCreator.canCreate(temporaryArcView.getSourceConnectable(), connectable);
    }

    private <T extends Connectable> void createArc(T connectable) {

        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();
        arcCreator.create(temporaryArcView.getSourceConnectable(), connectable, token);
        PetriNetTab tab = applicationView.getCurrentTab();
        tab.remove(temporaryArcView);
        temporaryArcView = null;
    }


}

