package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.models.component.Connectable;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public abstract class TypeAction extends GuiAction
{
    //TODO: Eventually a handler can tell the GUI Action to do its thing
    // for each type of type clicked on.
    public abstract void doAction(MouseEvent event, PetriNetController petriNetController);
    public abstract <T extends Connectable> void doConnectableAction(T connectable, PetriNetController petriNetController);

    public TypeAction(String name, String tooltip, int key, int modifiers)
    {
        super(name, tooltip, key, modifiers);
    }

    public TypeAction(String name, String tooltip, String keystroke, boolean toggleable)
    {
        super(name, tooltip, keystroke, toggleable);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationModel pipeApplicationView = ApplicationSettings.getApplicationModel();
        PipeApplicationModel model = ApplicationSettings.getApplicationModel();
        model.selectTypeAction(this);

//        pipeApplicationView.setMode(typeID);
        StatusBar statusBar = ApplicationSettings.getApplicationView().statusBar;
//        statusBar.changeText(typeID);

        PetriNetTab petriNetTab = ApplicationSettings.getApplicationView().getCurrentTab();
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        SelectionManager selectionManager = controller.getSelectionManager(petriNetTab);
        if(petriNetTab == null)
        {
            return;
        }

        //TODO: DO I NEED THIS?
        selectionManager.disableSelection();
        // _petriNetTabView.getSelectionObject().clearSelection();

//        if((typeID != Constants.ARC))// && (petriNetController.isCurrentlyCreatingArc()))
//        {
////            petriNetController.cancelArcCreation();
//            petriNetTab.repaint();
//        }
//
//        if(typeID == Constants.SELECT)
//        {
//            // disable drawing to eliminate possiblity of connecting arc to
//            // old coord of moved component
//            statusBar.changeText(typeID);
//            selectionManager.enableSelection();
//            petriNetTab.setCursorType("arrow");
//        }
//        else if(typeID == Constants.DRAG)
//        {
//            petriNetTab.setCursorType("move");
//        }
//        else
//        {
//            petriNetTab.setCursorType("crosshair");
//        }
    }

}
