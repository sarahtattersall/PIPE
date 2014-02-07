package pipe.handlers;

import pipe.actions.petrinet.SplitArcAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.Connectable;
import pipe.views.ArcView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class used to implement methods corresponding to mouse events on arcs.
 */
public class ArcHandler<S extends Connectable, T extends Connectable>
        extends PetriNetObjectHandler<Arc<S, T>, ArcView<S,T>> {


    public ArcHandler(ArcView<S,T> view, Container contentpane, Arc<S, T> component, PetriNetController controller) {
        super(view, contentpane, component, controller);
        enablePopup = true;
    }

    /**
     * Creates the popup menu that the user will see when they right click on a
     * component
     */
    @Override
    public JPopupMenu getPopup(MouseEvent e) {
        int popupIndex = 0;
        JMenuItem menuItem;
        JPopupMenu popup = super.getPopup(e);


        MouseEvent accurateEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e,
                ApplicationSettings.getApplicationView().getCurrentTab());
        menuItem = new JMenuItem(new SplitArcAction(petriNetController.getArcController(component),
                accurateEvent.getPoint()));
        menuItem.setText("Split Arc Segment");
        popup.insert(menuItem, popupIndex++);

        popup.insert(new JPopupMenu.Separator(), popupIndex++);

        if (component.getType().equals(ArcType.NORMAL)) {
            menuItem = new JMenuItem("Edit Weight");
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewComponent.showEditor();
                }
            });
            popup.insert(menuItem, popupIndex++);

            popup.insert(new JPopupMenu.Separator(), popupIndex);
        }
        return popup;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        //       if (!ApplicationSettings.getApplicationModel().isEditionAllowed()){
        //         return;
        //      }
        //      if (e.getClickCount() == 2){
        //         ArcView arcView = (ArcView) component;
        //         if (e.isControlDown()){
        //             ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
        //                    arcView.getArcPath().insertPointAt(
        //                            new Point2D.Float(arcView.getX() + e.getX(),
        //                            arcView.getY() + e.getY()), e.isAltDown()));
        //         } else {
        //            arcView.getSource().select();
        //            arcView.getTarget().select();
        //            justSelected = true;
        //         }
        //      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //       switch (ApplicationSettings.getApplicationModel().getMode()) {
        //         case Constants.SELECT:
        //            if (!isDragging){
        //               break;
        //            }
        //            ArcView currentObject = (ArcView) component;
        //            Point oldLocation = currentObject.getLocation();
        //            // Calculate translation in mouse
        //            int transX = Grid.getModifiedValue(e.getX() - dragInit.x);
        //            int transY = Grid.getModifiedY(e.getY() - dragInit.y);
        //            ((PetriNetTab)contentPane).getSelectionObject().translateSelection(
        //                     transX, transY);
        //            dragInit.translate(
        //                     -(currentObject.getLocation().x - oldLocation.x - transX),
        //                     -(currentObject.getLocation().y - oldLocation.y - transY));
        //      }
    }

    // Alex Charalambous: No longer does anything since you can't simply increment
    // the weight of the arc because multiple weights for multiple colours exist
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
