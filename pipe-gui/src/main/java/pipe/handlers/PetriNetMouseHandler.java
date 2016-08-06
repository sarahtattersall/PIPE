package pipe.handlers;

import pipe.actions.gui.CreateAction;
import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;

import javax.swing.*;
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
    /**
     * Main PIPE application model
     */
    private final PipeApplicationModel applicationModel;

    /**
     * Petri net tab
     */
    private final PetriNetTab petriNetTab;

    /**
     * Starting location of the petri net components drag
     */
    private Point dragStart = new Point(0,0);

    /**
     * Main PIPE application controller
     */
    private PetriNetController petriNetController;

    /**
     *
     * @param applicationModel main PIPE application model
     * @param controller main PIPE application controller
     * @param petriNetTab Petri net tab
     */
    public PetriNetMouseHandler(PipeApplicationModel applicationModel, PetriNetController controller, PetriNetTab petriNetTab) {
        super();
        this.applicationModel = applicationModel;
        this.petriNetTab = petriNetTab;
        this.petriNetController = controller;
    }

    /**
     * Performs the corresponding selected toolbar action on the component
     * @param event mouse event 
     */
    @Override
    public void mousePressed(MouseEvent event) {
        if (SwingUtilities.isLeftMouseButton(event)) {
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

    /**
     * Changes the cursor to a cross hair
     * @param e mouse event 
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * Noop
     * @param e mouse event 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // No action needed
    }

    /**
     * Sets the starting drag point
     * @param e mouse event 
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        petriNetTab.drag(dragStart, e.getPoint());
    }

    /**
     * Performs the movement action of the toolbar action selected on the component
     * @param event mouse event 
     */
    @Override
    public void mouseMoved(MouseEvent event) {
        doAction(event);
    }

}
