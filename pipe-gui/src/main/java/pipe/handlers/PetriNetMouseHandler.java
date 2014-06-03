package pipe.handlers;

import pipe.actions.gui.CreateAction;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.actions.gui.PipeApplicationModel;
import pipe.handlers.mouse.MouseUtilities;

import javax.swing.event.MouseInputAdapter;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * PetriNetMouseHandler handles mouse press inputs on a given petri net tab.
 * It works out what action is selected (e.g. create new place) and makes this happen.
 */
public class PetriNetMouseHandler extends MouseInputAdapter {
    private final PipeApplicationModel applicationModel;

    private final PetriNetTab petriNetTab;

    private final MouseUtilities mouseUtilities;

    private Point dragStart;

    private PetriNetController petriNetController;

    public PetriNetMouseHandler(PipeApplicationModel applicationModel, MouseUtilities mouseUtilities,
                                PetriNetController controller, PetriNetTab petriNetTab) {
        super();
        this.applicationModel = applicationModel;
        this.petriNetTab = petriNetTab;
        this.petriNetController = controller;
        this.mouseUtilities = mouseUtilities;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (mouseUtilities.isLeftMouse(event)) {
            doAction(event);
        }
    }

    /**
     * Performs action on the petri net if an aciton
     * has been selected and if the petri net is not in animation mode
     *
     * @param event mouse event
     */
    private void doAction(MouseEvent event) {

        CreateAction action = applicationModel.getSelectedAction();
        if (action != null && !applicationModel.isInAnimationMode()) {
            action.doAction(event, petriNetController);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        petriNetTab.drag(dragStart, e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        doAction(event);
    }

}
