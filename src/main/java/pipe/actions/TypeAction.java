package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.StatusBar;
import pipe.models.PipeApplicationModel;

import java.awt.event.ActionEvent;

public class TypeAction extends GuiAction
{
    private final int typeID;

    public TypeAction(String name, int typeID, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
        this.typeID = typeID;
    }

    public TypeAction(String name, int typeID, String tooltip, String keystroke, boolean toggleable)
    {
        super(name, tooltip, keystroke, toggleable);
        this.typeID = typeID;
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationModel pipeApplicationView = ApplicationSettings.getApplicationModel();
        // if (!isSelected()){
        this.setSelected(true);

        // deselect other actions
        if(this != pipeApplicationView.placeAction)
        {
            pipeApplicationView.placeAction.setSelected(false);
        }
        if(this != pipeApplicationView.transAction)
        {
            pipeApplicationView.transAction.setSelected(false);
        }
        if(this != pipeApplicationView.timedtransAction)
        {
            pipeApplicationView.timedtransAction.setSelected(false);
        }
        if(this != pipeApplicationView.arcAction)
        {
            pipeApplicationView.arcAction.setSelected(false);
        }
        if(this != pipeApplicationView.inhibarcAction)
        {
            pipeApplicationView.inhibarcAction.setSelected(false);
        }
        if(this != pipeApplicationView.tokenAction)
        {
            pipeApplicationView.tokenAction.setSelected(false);
        }
        if(this != pipeApplicationView.deleteTokenAction)
        {
            pipeApplicationView.deleteTokenAction.setSelected(false);
        }
        if(this != pipeApplicationView.rateAction)
        {
            pipeApplicationView.rateAction.setSelected(false);
        }
        if(this != pipeApplicationView.selectAction)
        {
            pipeApplicationView.selectAction.setSelected(false);
        }
        if(this != pipeApplicationView.annotationAction)
        {
            pipeApplicationView.annotationAction.setSelected(false);
        }
        if(this != pipeApplicationView.dragAction)
        {
            pipeApplicationView.dragAction.setSelected(false);
        }

        pipeApplicationView.setMode(typeID);
        StatusBar statusBar = ApplicationSettings.getApplicationView().statusBar;
        statusBar.changeText(typeID);

        PetriNetTab petriNetTab = ApplicationSettings.getApplicationView().getCurrentTab();
        if(petriNetTab == null)
        {
            return;
        }

        petriNetTab.getSelectionObject().disableSelection();
        // _petriNetTabView.getSelectionObject().clearSelection();




        PetriNetController petriNetController = petriNetTab.getPetriNetController();
        if((typeID != Constants.ARC) && (petriNetController.isCurrentlyCreatingArc()))
        {
            petriNetController.cancelArcCreation();
            petriNetTab.repaint();
        }

        if(typeID == Constants.SELECT)
        {
            // disable drawing to eliminate possiblity of connecting arc to
            // old coord of moved component
            statusBar.changeText(typeID);
            petriNetTab.getSelectionObject().enableSelection();
            petriNetTab.setCursorType("arrow");
        }
        else if(typeID == Constants.DRAG)
        {
            petriNetTab.setCursorType("move");
        }
        else
        {
            petriNetTab.setCursorType("crosshair");
        }
    }
    // }

}
