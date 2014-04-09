package pipe.handlers;

import pipe.actions.petrinet.DeletePetriNetObjectAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.models.component.PetriNetComponent;
import pipe.views.AbstractPetriNetViewComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

//import java.awt.event.MouseWheelEvent; eliminat NOU-PERE


/**
 * Class used to implement methods corresponding to mouse events on all
 * PetriNetObjects.
 *
 * @author unknown
 */
public class PetriNetObjectHandler<T extends PetriNetComponent, V extends AbstractPetriNetViewComponent<T>>
        extends javax.swing.event.MouseInputAdapter {
    // justSelected: set to true on press, and false on release;
    static boolean justSelected = false;

    final protected Container contentPane;

    protected final PetriNetController petriNetController;

    final T component;

    final V viewComponent;

    final DragManager dragManager;

    boolean isDragging = false;

    boolean enablePopup = false;

    Point dragInit = new Point();

    private int totalX = 0;

    private int totalY = 0;

    // constructor passing in all required objects
    PetriNetObjectHandler(V viewComponent, Container contentpane, T component, PetriNetController controller) {
        this.viewComponent = viewComponent;
        contentPane = contentpane;
        this.component = component;
        //TODO: PASS INTO CTR
        petriNetController = controller;
        dragManager = petriNetController.getDragManager();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
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
            m.show(viewComponent, 0, 0);
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
        JMenuItem menuItem = new JMenuItem(new DeletePetriNetObjectAction(component));
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
        // Have to check for popup here as well as on pressed for crossplatform!!
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        if (applicationModel.isEditionAllowed() && enablePopup) {
            checkForPopup(e);
        }

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (applicationModel.getMode() == Constants.SELECT) {
            if (!justSelected) {
                if (e.isShiftDown()) {
                    petriNetController.deselect(component);
                } else {
                    PipeApplicationController controller = ApplicationSettings.getApplicationController();
                    SelectionManager selectionManager = controller.getSelectionManager((PetriNetTab) contentPane);

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

        if (ApplicationSettings.getApplicationModel().getMode() == Constants.SELECT) {
            if (component.isDraggable()) {
                if (!isDragging) {
                    isDragging = true;
                }
            }
            if (!e.isConsumed()) {
                dragManager.drag(e.getPoint());
            }
        }
    }

    //NOU-PERE: eliminat mouseWheelMoved
}
