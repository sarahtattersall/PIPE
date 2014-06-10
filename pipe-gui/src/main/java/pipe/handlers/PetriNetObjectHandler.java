package pipe.handlers;

import pipe.actions.gui.DeletePetriNetComponentAction;
import pipe.actions.gui.PipeApplicationModel;
import pipe.constants.GUIConstants;
import pipe.controllers.DragManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.SelectionManager;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.*;
import java.awt.Container;
import java.awt.event.MouseEvent;

/**
 * Class used to implement methods corresponding to mouse events on all
 * PetriNetObjects.
 *
 * @author unknown
 */
public class PetriNetObjectHandler<T extends PetriNetComponent> extends javax.swing.event.MouseInputAdapter {
    // justSelected: set to true on press, and false on release;
    private static boolean justSelected = false;

    /**
     * Container of the component
     */
    protected final Container contentPane;

    /**
     * Controller for the Petri net the component is contained in
     */
    protected final PetriNetController petriNetController;

    /**
     * Main PIPE application model
     */
    protected final PipeApplicationModel applicationModel;

    /**
     * Petri net component
     */
    protected final T component;

    /**
     * Drag manager for the Petri ent
     */
    protected final DragManager dragManager;

    /**
     * true if dragging is taking place
     */
    protected boolean isDragging = false;

    /**
     * true if pop ups are enabled
     */
    protected boolean enablePopup = false;

    /**
     * Constructor
     * @param contentPane parent pane of the view of the component
     * @param component Petri net component
     * @param controller Petri net controller for the Petri net the component is contained in
     * @param applicationModel main PIPE application model
     */
    PetriNetObjectHandler(Container contentPane, T component, PetriNetController controller,
                          PipeApplicationModel applicationModel) {
        this.contentPane = contentPane;
        this.component = component;
        petriNetController = controller;
        this.applicationModel = applicationModel;
        dragManager = petriNetController.getDragManager();
    }

    /**
     * When the mouse is pressed on a component if editions and pop ups
     * are allowed then the pop up menu appears
     * @param e
     */
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

        if (applicationModel.getMode() == GUIConstants.SELECT && !justSelected) {
            if (e.isShiftDown()) {
                petriNetController.deselect(component);
            } else {
                SelectionManager selectionManager = petriNetController.getSelectionManager();
                selectionManager.clearSelection();
                petriNetController.deselect(component);
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
            if (component.isDraggable() && !isDragging) {
                isDragging = true;
                dragManager.saveStartingDragCoordinates();
            }
        }
        if (!e.isConsumed()) {
            MouseEvent newEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, contentPane);
            dragManager.drag(newEvent.getPoint());
        }
    }
}

