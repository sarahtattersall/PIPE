package pipe.handlers;

import pipe.actions.DeletePetriNetObjectAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.SelectionManager;
import pipe.gui.model.PipeApplicationModel;
import pipe.models.component.PetriNetComponent;
import pipe.utilities.gui.GuiUtils;
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
    final protected Container contentPane;
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
    PetriNetObjectHandler(V viewComponent, Container contentpane, T component, PetriNetController controller) {
        this.viewComponent = viewComponent;
        contentPane = contentpane;
        this.component = component;
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
        if (applicationModel.isEditionAllowed() && enablePopup) {
            checkForPopup(e);
        }

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        if (applicationModel.getMode() == Constants.SELECT) {
            if (!petriNetController.isSelected(component)) {
                if (!e.isShiftDown()) {
                    PipeApplicationController controller = ApplicationSettings.getApplicationController();
                    SelectionManager selectionManager = controller.getSelectionManager((PetriNetTab) contentPane);
                    selectionManager.clearSelection();
                }
                petriNetController.select(component);
                justSelected = true;
            }


            MouseEvent accurateEvent = GuiUtils.getAccurateMouseEvent(contentPane, e);
            dragInit = accurateEvent.getPoint();
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
                    PipeApplicationController controller = ApplicationSettings.getApplicationController();
                    SelectionManager selectionManager = controller.getSelectionManager((PetriNetTab) contentPane);

                    selectionManager.clearSelection();
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

            MouseEvent accurateEvent = GuiUtils.getAccurateMouseEvent(contentPane, e);


            // Calculate translation in mouse
            int transX = accurateEvent.getX() - dragInit.x;
            int transY = accurateEvent.getY() - dragInit.y;
            dragInit = accurateEvent.getPoint();

            PipeApplicationController controller = ApplicationSettings.getApplicationController();
            SelectionManager selectionManager = controller.getSelectionManager((PetriNetTab) contentPane);

            selectionManager.translateSelection(transX, transY);
        }
    }

    //NOU-PERE: eliminat mouseWheelMoved
}
