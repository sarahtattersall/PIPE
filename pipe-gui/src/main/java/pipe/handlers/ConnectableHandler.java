package pipe.handlers;

import pipe.actions.gui.create.CreateAction;
import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.*;
import java.awt.Container;
import java.awt.event.MouseEvent;

/**
 * ConnectableHandler handles mouse clicks on Connectables.
 */
public class ConnectableHandler<T extends Connectable>
        extends PetriNetObjectHandler<T> {


    // constructor passing in all required objects
    ConnectableHandler(Container contentpane, T obj, PetriNetController controller, PipeApplicationModel applicationModel) {
        super(contentpane, obj, controller, applicationModel);
        enablePopup = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (e.isPopupTrigger()) {
            JPopupMenu menu = getPopup(e);
            menu.show(e.getComponent(), 0, 0);
        } else if (e.getButton() == MouseEvent.BUTTON1) {

            CreateAction selectedAction = applicationModel.getSelectedAction();
            selectedAction.doConnectableAction(component, petriNetController);
        }
    }

    /**
     * getPopup adds menu items which connectables all share
     * @param e event
     * @return new JPopupMenu with hide and show attributes and supers attributes
     */
    @Override
    protected JPopupMenu getPopup(MouseEvent e) {
        JPopupMenu popupMenu = super.getPopup(e);
//        JMenuItem menuItem = new JMenuItem(new ShowHideInfoAction<>(viewComponent));
//        if (viewComponent.getAttributesVisible()){
//            menuItem.setText("Hide Attributes");
//        } else {
//            menuItem.setText("Show Attributes");
//        }
//        popupMenu.insert(menuItem, 0);
        return popupMenu;
    }
}
