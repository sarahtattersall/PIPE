package pipe.controllers;

import pipe.actions.ActionEnum;
import pipe.actions.file.FileAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.CopyPasteManager;
import pipe.gui.PetriNetTab;
import pipe.models.PipeApplicationModel;
import pipe.views.PetriNetView;

import java.util.ArrayList;

public class PipeApplicationController
{
    private PetriNetController _petriNetController;
    private final CopyPasteManager _copyPasteManager;
    private final PipeApplicationModel applicationModel;

    public PipeApplicationController(PipeApplicationModel applicationModel)
    {
        _petriNetController = new PetriNetController();
        this.applicationModel = applicationModel;
        _copyPasteManager = new CopyPasteManager();
        ApplicationSettings.register(this);
    }

    public FileAction getFileAction(ActionEnum actionType) {
        return actionType.get(applicationModel);
    }

    public CopyPasteManager getCopyPasteManager()
    {
        return _copyPasteManager;
    }

    public boolean isPasteEnabled()
    {
        return _copyPasteManager.pasteEnabled();
    }

    public boolean isPasteInProgress()
    {
        return _copyPasteManager.pasteInProgress();
    }

    public void cancelPaste()
    {
        _copyPasteManager.cancelPaste();
    }

    public void copy(ArrayList selection, PetriNetTab appView)
    {
        _copyPasteManager.doCopy(selection, appView);
    }

    public void showPasteRectangle(PetriNetTab appView)
    {
        _copyPasteManager.showPasteRectangle(appView);
    }


    public int addEmptyPetriNetTo(ArrayList<PetriNetTab> petriNetTabs)
    {
        PetriNetView petriNetView = _petriNetController.addEmptyPetriNet();
        PetriNetTab petriNetTab = new PetriNetTab(petriNetView);
        petriNetTabs.add(petriNetTab);
        return petriNetTabs.size() - 1;
    }

    public PetriNetController getPetriNetController()
    {
        return _petriNetController;
    }
}
