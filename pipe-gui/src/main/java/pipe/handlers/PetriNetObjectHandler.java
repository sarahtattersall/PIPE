package pipe.handlers;

import pipe.actions.gui.DeletePetriNetComponentAction;
import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import pipe.gui.DragManager;
import pipe.gui.SelectionManager;
import pipe.gui.model.PipeApplicationModel;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.*;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Class used to implement methods corresponding to mouse events on all
 * PetriNetObjects.
 *
 * @author unknown
 */
public class PetriNetObjectHandler<T extends PetriNetComponent>
        extends javax.swing.event.MouseInputAdapter {
    // justSelected: set to true on press, and false on release;
    static boolean justSelected = false;

    final protected Container contentPane;

    protected final PetriNetController petriNetController;

    protected final PipeApplicationModel applicationModel;

    final T component;

    final DragManager dragManager;

    boolean isDragging = false;

    boolean enablePopup = false;

    Point dragInit = new Point();

    private int totalX = 0;

    private int totalY = 0;

    // constructor passing in all required objects
    PetriNetObjectHandler(Container contentPane, T component, PetriNetController controller, PipeApplicationModel applicationModel) {
        this.contentPane = contentPane;
        this.component = component;
        petriNetController = controller;
        this.applicationModel = applicationModel;
        dragManager = petriNetController.getDragManager();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (applicationModel.isEditionAllowed() && enablePopup) {
            checkForPopup(e);
        }
    }

    /**
     * Displays the popup menu in the top left
     * of the item
     *
     * @param e event
     */
    private void checkForPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu m = getPopup(e);
            m.show(e.getComponent(), 0, 0);
        }
    }

    /**
     * Creates the popup menu that the user will see when they right click on a
     * component
     *
     * @param e
     * @return
     */
    protected JPopupMenu getPopup(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(new DeletePetriNetComponentAction(component, petriNetController));
        menuItem.setText("Delete");
        popup.add(menuItem);
        return popup;
    }

    /**
     * Event handler for when the user releases the mouse, used in conjunction
     * with mouseDragged and mouseReleased to implement the moving action
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragging) {
            dragManager.finishDrag();
        }
        isDragging = false;
        // Have to check for popup here as well as on pressed for crossplatform!!
        if (applicationModel.isEditionAllowed() && enablePopup) {
            checkForPopup(e);
        }

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (applicationModel.getMode() == GUIConstants.SELECT) {
            if (!justSelected) {
                if (e.isShiftDown()) {
                    petriNetController.deselect(component);
                } else {
                    SelectionManager selectionManager = petriNetController.getSelectionManager();
                    selectionManager.clearSelection();
                    petriNetController.deselect(component);
                }
            }
        }
        justSelected = false;
    }

    /**
     * Handler for dragging objects around
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (applicationModel.getMode() == GUIConstants.SELECT) {
            if (component.isDraggable()) {
                if (!isDragging) {
                    isDragging = true;
                    dragManager.saveStartingDragCoordinates();
                }
            }
            if (!e.isConsumed()) {
                dragManager.drag(e.getPoint());
            }
        }
    }

    //NOU-PERE: eliminat mouseWheelMoved
}
