package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.create.CreateAction;

public interface ActionManager {
    Iterable<GuiAction> getActions();

    void enableActions();

    void disableActions();
}
