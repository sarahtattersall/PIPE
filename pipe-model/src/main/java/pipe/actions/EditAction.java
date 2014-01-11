package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

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
        PetriNetController controller = applicationController.getActivePetriNetController();
        if(applicationModel.isEditionAllowed())
        {
            //TODO: REIMPLEMENT THE BELOW COMMENTED METHODS
//            if(this == applicationModel.cutAction)
//            {
//                ArrayList selection = appView.getSelectionObject()
//                        .getSelection();
//
//                applicationController.copy(selection, appView);
//                controller.getHistoryManager().newEdit(); // new "transaction""
//                controller.getHistoryManager().deleteSelection(selection);
//                controller.getSelectionObject().deleteSelection();
//                applicationModel.pasteAction.setEnabled(applicationController.isPasteEnabled());
//            }
//            else if(this == applicationModel.copyAction)
//            {
//                applicationController.copy(appView.getSelectionObject().getSelection(), appView);
//                applicationModel.pasteAction.setEnabled(applicationController.isPasteEnabled());
//            }
//            else if(this == applicationModel.pasteAction)
//            {
//                appView.getSelectionObject().clearSelection();
//                applicationController.showPasteRectangle(appView);
//            }
//            if(this == applicationModel.undoAction)
//            {
//                controller.getHistoryManager().doUndo();
//            }
//            else if(this == applicationModel.redoAction)
//            {
//                controller.getHistoryManager().doRedo();
//            }
        }
    }
}
