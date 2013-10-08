package pipe.actions;

import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.views.ArcView;
import pipe.views.PipeApplicationView;
import pipe.views.PlaceView;
import pipe.models.PipeApplicationModel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EditAction extends GuiAction
{

    public EditAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        PipeApplicationController applicationController = ApplicationSettings.getApplicationController();
        PetriNetTab appView = pipeApplicationView.getCurrentTab();
        if(applicationModel.isEditionAllowed())
        {
            if(this == applicationModel.cutAction)
            {
                ArrayList selection = appView.getSelectionObject()
                        .getSelection();
//                for(Object item : selection){
//                	if(item instanceof ArcView){
//                		System.out.println("arcdetected");
//                	}
//                }
//               System.out.println("item number:"+selection.size());
                
                applicationController.copy(selection, appView);
                appView.getHistoryManager().newEdit(); // new "transaction""
                appView.getHistoryManager().deleteSelection(selection);
                appView.getSelectionObject().deleteSelection();
                applicationModel.pasteAction.setEnabled(applicationController.isPasteEnabled());
            }
            else if(this == applicationModel.copyAction)
            {
                applicationController.copy(appView.getSelectionObject().getSelection(), appView);
                applicationModel.pasteAction.setEnabled(applicationController.isPasteEnabled());
            }
            else if(this == applicationModel.pasteAction)
            {
                appView.getSelectionObject().clearSelection();
                applicationController.showPasteRectangle(appView);
            }
            else if(this == applicationModel.undoAction)
            {
                appView.getHistoryManager().doUndo();
            }
            else if(this == applicationModel.redoAction)
            {
                appView.getHistoryManager().doRedo();
            }
        }
    }
}
