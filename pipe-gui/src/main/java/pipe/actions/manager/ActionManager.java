package pipe.actions.manager;

import pipe.actions.gui.GuiAction;

public interface ActionManager {
    Iterable<GuiAction> getActions();

    void enableActions();

    void disableActions();
}
