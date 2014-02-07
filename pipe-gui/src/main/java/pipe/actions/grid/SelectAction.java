package pipe.actions.grid;

import pipe.actions.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.gui.SelectionManager;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SelectAction extends GuiAction {
    private final PipeApplicationView pipeApplicationView;

    private final PipeApplicationController pipeApplicationController;

    public SelectAction(PipeApplicationView pipeApplicationView, PipeApplicationController pipeApplicationController) {
        super("Select", "Select components (alt-S)", KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationView = pipeApplicationView;
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (pipeApplicationView.areAnyTabsDisplayed()) {
            PetriNetTab petriNetTab = pipeApplicationView.getCurrentTab();
            SelectionManager selectionManager = pipeApplicationController.getSelectionManager(petriNetTab);
            selectionManager.enableSelection();
            petriNetTab.setCursorType("arrow");
        }
    }
}
