package pipe.handlers;

import pipe.actions.ShowHideInfoAction;
import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.historyActions.HistoryManager;
import pipe.models.PipeApplicationModel;
import pipe.models.component.Connectable;
import pipe.models.component.Place;
import pipe.models.component.Transition;
import pipe.views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * ConnectableHandler handles mouse clicks on Connectables.
 */
public class ConnectableHandler<T extends Connectable, V extends ConnectableView>
        extends PetriNetObjectHandler<T, V> {


    private static boolean mouseDown = false;

    public static boolean isMouseDown() {
        return mouseDown;
    }

    // constructor passing in all required objects
    ConnectableHandler(V view, Container contentpane,
                       T obj, PetriNetController controller) {
        super(view, contentpane, obj, controller);
        enablePopup = true;
    }

    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        mouseDown = true;

        if (e.isPopupTrigger()) {
            JPopupMenu menu = getPopup(e);
            menu.show(viewComponent, 0, 0);
        } else if (e.getButton() == MouseEvent.BUTTON1) {

            PipeApplicationModel model = ApplicationSettings.getApplicationModel();
            TypeAction selectedAction = model.getSelectedAction();
            selectedAction.doConnectableAction(component, petriNetController);
        }
    }

    @Override
    protected JPopupMenu getPopup(MouseEvent e) {
        JPopupMenu popupMenu = super.getPopup(e);
        JMenuItem menuItem = new JMenuItem(new ShowHideInfoAction(viewComponent));
        if (viewComponent.getAttributesVisible()){
            menuItem.setText("Hide Attributes");
        } else {
            menuItem.setText("Show Attributes");
        }
        popupMenu.insert(menuItem, 0);
        return popupMenu;
    }
}
