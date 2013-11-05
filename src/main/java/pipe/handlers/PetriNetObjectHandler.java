package pipe.handlers;

import pipe.actions.DeletePetriNetObjectAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNetComponent;
import pipe.models.PipeApplicationModel;
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
public class PetriNetObjectHandler<T extends PetriNetComponent>
        extends javax.swing.event.MouseInputAdapter
{
    final Container contentPane;
    final T component;

    // justSelected: set to true on press, and false on release;
    static boolean justSelected = false;

    boolean isDragging = false;
    boolean enablePopup = false;
    Point dragInit = new Point();

    private int totalX = 0;
    private int totalY = 0;
    private final PetriNetController petriNetController;

    // constructor passing in all required objects
    PetriNetObjectHandler(Container contentpane, T obj)
    {
        contentPane = contentpane;
        component = obj;
        //TODO: PASS INTO CTR
        petriNetController = ApplicationSettings.getPetriNetController();
    }


    /**
     * Creates the popup menu that the user will see when they right click on a
     * component
     * @param e
     * @return
     */
    JPopupMenu getPopup(MouseEvent e)
    {
        JPopupMenu popup = new JPopupMenu();
//        JMenuItem menuItem =
//                new JMenuItem(new DeletePetriNetObjectAction(component));
//        menuItem.setText("Delete");
//        popup.add(menuItem);
        return popup;
    }


    /**
     * Displays the popup menu
     * @param e
     */
    private void checkForPopup(MouseEvent e)
    {
        if(SwingUtilities.isRightMouseButton(e))
        {
            JPopupMenu m = getPopup(e);
            if(m != null)
            {
                //m.show(component, e.getX(), e.getY());
            }
        }
    }


    public void mousePressed(MouseEvent e)
    {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        if(applicationModel.isEditionAllowed() && enablePopup)
        {
            //checkForPopup(e);
        }

        if(!SwingUtilities.isLeftMouseButton(e))
        {
            return;
        }

        if(applicationModel.getMode() == Constants.SELECT)
        {
            if(!petriNetController.isSelected(component))
            {
                if(!e.isShiftDown())
                {
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
    public void mouseReleased(MouseEvent e)
    {
        // Have to check for popup here as well as on pressed for crossplatform!!
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        if(applicationModel.isEditionAllowed() && enablePopup)
        {
            checkForPopup(e);
        }

        if(!SwingUtilities.isLeftMouseButton(e))
        {
            return;
        }

        if(applicationModel.getMode() == Constants.SELECT)
        {
            if(isDragging)
            {
                isDragging = false;
                ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().translateSelection(
                        ((PetriNetTab) contentPane).getSelectionObject().getSelection(),
                        totalX,
                        totalY);
                totalX = 0;
                totalY = 0;
            }
            else
            {
                if(!justSelected)
                {
                    if(e.isShiftDown())
                    {
                        petriNetController.deselect(component);
                    }
                    else
                    {
                        ((PetriNetTab) contentPane).getSelectionObject().clearSelection();
                        petriNetController.select(component);
                    }
                }
            }
        }
        justSelected = false;
    }


    /**
     * Handler for dragging PlaceTransitionObjects around
     */
    public void mouseDragged(MouseEvent e)
    {

        if(!SwingUtilities.isLeftMouseButton(e))
        {
            return;
        }

        if(ApplicationSettings.getApplicationModel().getMode() == Constants.SELECT)
        {
            if(component.isDraggable())
            {
                if(!isDragging)
                {
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
