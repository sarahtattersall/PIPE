package pipe.actions.manager;

import pipe.actions.gui.GuiAction;

/**
 * Manager to store specific groups of tool bar acitons
 */
public interface ActionManager {
    /**
     *
     * @return the actions stored by the manager
     */
    Iterable<GuiAction> getActions();

    /**
     * Enable the actions stored by the manager
     */
    void enableActions();

    /**
     * Disable the actions stored by the manager
     */
    void disableActions();
}
