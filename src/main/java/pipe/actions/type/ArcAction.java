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
import pipe.views.TemporaryArcView;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ArcAction extends TypeAction {

    private final ArcSourceVisitor sourceVisitor;
    private final ArcActionCreator arcCreator;
    private TemporaryArcView<? extends Connectable> temporaryArcView = null;

    public ArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke, ArcSourceVisitor sourceVisitor, ArcActionCreator arcCreator) {
        super(name, typeID, tooltip, keystroke);
        this.sourceVisitor = sourceVisitor;
        this.arcCreator = arcCreator;
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
            MouseEvent accurateEvent = SwingUtilities.convertMouseEvent(event.getComponent(), event,
                    ApplicationSettings.getApplicationView().getCurrentTab());

            temporaryArcView.setEnd(accurateEvent.getPoint());
            PetriNetTab tab = ApplicationSettings.getApplicationView().getCurrentTab();
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
            PetriNetTab tab = ApplicationSettings.getApplicationView().getCurrentTab();
            tab.add(temporaryArcView);
        }  else if (temporaryArcView != null && canCreateArcHere(connectable)) {
            createArc(connectable);
        }
    }

    private <T extends Connectable> boolean canCreateArcHere(T connectable) {
        return arcCreator.canCreate(temporaryArcView.getSourceConnectable(), connectable);
    }

    private <T extends Connectable> void createArc(T connectable) {

        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();
        arcCreator.create(temporaryArcView.getSourceConnectable(), connectable, token);
        PetriNetTab tab = ApplicationSettings.getApplicationView().getCurrentTab();
        tab.remove(temporaryArcView);
        temporaryArcView = null;
    }


}

