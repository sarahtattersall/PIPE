package pipe.handlers;

import pipe.actions.DeletePetriNetObjectAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.models.PipeApplicationModel;
import pipe.models.component.PetriNetComponent;
import pipe.views.PetriNetViewComponent;

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
public class PetriNetObjectHandler<T extends PetriNetComponent, V extends PetriNetViewComponent>
        extends javax.swing.event.MouseInputAdapter {
    final Container contentPane;
    final T component;
    final V viewComponent;

    // justSelected: set to true on press, and false on release;
    static boolean justSelected = false;

    boolean isDragging = false;
    boolean enablePopup = false;
    Point dragInit = new Point();

    private int totalX = 0;
    private int totalY = 0;
    protected final PetriNetController petriNetController;

    // constructor passing in all required objects
    PetriNetObjectHandler(V viewComponent, Container contentpane, T obj, PetriNetController controller) {
        this.viewComponent = viewComponent;
        contentPane = contentpane;
        component = obj;
        //TODO: PASS INTO CTR
        petriNetController = controller;
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
        JMenuItem menuItem =
                new JMenuItem(new DeletePetriNetObjectAction(component));
        menuItem.setText("Delete");
        popup.add(menuItem);
        return popup;
    }


    /**
     * Displays the popup menu
     *
     * @param e
     */
    private void checkForPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu m = getPopup(e);
            m.show(viewComponent, e.getX(), e.getY());
        }
    }


    public void mousePressed(MouseEvent e) {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        System.out.println(applicationModel.isEditionAllowed() && enablePopup);
        if (applicationModel.isEditionAllowed() && enablePopup) {
            checkForPopup(e);
        }

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (applicationModel.getMode() == Constants.SELECT) {
            if (!petriNetController.isSelected(component)) {
                if (!e.isShiftDown()) {
                    ((PetriNetTab) contentPane).getSelectionObject().clearSelection();
                }
                petriNetController.select(component);
                justSelected = true;
            }
            dragInit = e.getPoint();
        }
    }


    /**
     * Event handler for when the user releases the mouse, used in conjunction
     * with mouseDragged and mouseReleased to implement the moving action
     */
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
                    ((PetriNetTab) contentPane).getSelectionObject().clearSelection();
                    petriNetController.select(component);
                }
            }
        }
        justSelected = false;
    }


    /**
     * Handler for dragging objects around
     */
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

            // Calculate translation in mouse
            int transX = e.getX() - dragInit.x;//Grid.getModifiedX(e.getX() - dragInit.x);
            int transY = e.getY() - dragInit.y;//Grid.getModifiedY(e.getY() - dragInit.y);
            totalX += transX;
            totalY += transY;

            ((PetriNetTab) contentPane).getSelectionObject().translateSelection(
                    transX, transY);
        }
    }

    //NOU-PERE: eliminat mouseWheelMoved
}
