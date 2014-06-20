/*
 * Created on 28-Feb-2004
 * Author is Michael Camacho
 *
 */
package pipe.handlers;

import pipe.actions.gui.DeleteArcPathPointAction;
import pipe.actions.gui.PipeApplicationModel;
import pipe.actions.petrinet.SplitArcPointAction;
import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.views.ArcPathPoint;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;

import javax.swing.*;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;


/**
 * Arc path point handler
 */
public class ArcPathPointHandler extends PetriNetObjectHandler<ArcPoint> {


    /**
     * Arc controller that the path point belongs to
     */
    private final ArcController<?, ?> arcController;

    /**
     *
     * @param contentpane
     * @param arcPathPoint arc path point
     * @param controller Petri net controller the arc point belongs in
     * @param arcController arc controller the path point belongs to
     * @param applicationModel main PIPE application model
     */
    public ArcPathPointHandler(Container contentpane, ArcPathPoint arcPathPoint, PetriNetController controller,
                               ArcController<?, ?> arcController,  PipeApplicationModel applicationModel) {
        super(contentpane, arcPathPoint.getModel(), controller, applicationModel);
        this.arcController = arcController;
        enablePopup = true;
    }

    /**
     * Creates the popup menu that the user will see when they right click on a component
     */
    @Override
    public JPopupMenu getPopup(MouseEvent event) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem =
                new JMenuItem(new DeleteArcPathPointAction(component, arcController));
        menuItem.setText("Delete");
        popup.add(menuItem);
        //TODO: Put these back in!
//
//        if (!(viewComponent.isDeleteable())) {
//            popup.getComponent(0).setEnabled(false);
//        }
//
//        popup.insert(new JPopupMenu.Separator(), 0);
//
//        if (viewComponent.getIndex() > 0) {
//            menuItem = new JMenuItem(new ToggleArcPointAction(component, arcController));
//            if (!component.isCurved()) {
//                menuItem.setText("Change to Curved");
//            } else {
//                menuItem.setText("Change to Straight");
//            }
//            popup.insert(menuItem, 0);
//        }

        menuItem = new JMenuItem(new SplitArcPointAction(component, arcController));
        menuItem.setText("Split Point");
        popup.add(menuItem, 1);
        return popup;
    }

    /**
     * When the mouse is pressed on the arc point select it
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        ((ArcPathPoint) e.getComponent()).setVisibilityLock(true);
        petriNetController.select(component);
        MouseEvent newEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, contentPane);
        Point2D.Double point = new Point2D.Double(newEvent.getX(), newEvent.getY());
        dragManager.setDragStart(point);
        super.mousePressed(e);
    }

    /**
     * When the mouse is released on the arc point, deselect it
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        ((ArcPathPoint) e.getComponent()).setVisibilityLock(false);
        petriNetController.deselect(component);
        super.mouseReleased(e);
    }

    /**
     * When the mouse is dragged on an arc point, move it
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (component.isDraggable()) {
            super.mouseDragged(e);
        }
    }

    /**
     * Noop action
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // No action
    }

}
