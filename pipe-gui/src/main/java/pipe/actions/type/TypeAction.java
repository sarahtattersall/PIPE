package pipe.actions.type;

import pipe.actions.GuiAction;
import pipe.actions.grid.SelectAction;
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
        PipeApplicationModel model = ApplicationSettings.getApplicationModel();
        model.selectTypeAction(this);

        PetriNetTab petriNetTab = ApplicationSettings.getApplicationView().getCurrentTab();
        petriNetTab.setCursorType("crosshair");

        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        SelectionManager selectionManager = controller.getSelectionManager(petriNetTab);
        selectionManager.disableSelection();
    }

}
