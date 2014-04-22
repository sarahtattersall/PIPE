package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.DragManager;
import pipe.gui.PetriNetTab;
import pipe.gui.SelectionManager;
import pipe.models.component.Connectable;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SelectAction extends CreateAction {
    private final PipeApplicationView pipeApplicationView;

    private final PipeApplicationController pipeApplicationController;

    public SelectAction(PipeApplicationView pipeApplicationView, PipeApplicationController pipeApplicationController) {
        super("Select", "Select components (alt-S)", KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationView = pipeApplicationView;
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {

    }

    @Override
    public <T extends Connectable> void doConnectableAction(T connectable, PetriNetController petriNetController) {
        if (!petriNetController.isSelected(connectable)) {
            SelectionManager selectionManager = pipeApplicationController.getSelectionManager(pipeApplicationView.getCurrentTab());
            selectionManager.clearSelection();
        }
        petriNetController.select(connectable);
        DragManager dragManager = petriNetController.getDragManager();
        dragManager.setDragStart(connectable.getCentre());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (pipeApplicationView.areAnyTabsDisplayed()) {
            PetriNetTab petriNetTab = pipeApplicationView.getCurrentTab();
            SelectionManager selectionManager = pipeApplicationController.getSelectionManager(petriNetTab);
            selectionManager.enableSelection();
            petriNetTab.setCursorType("arrow");
        }
    }
}
