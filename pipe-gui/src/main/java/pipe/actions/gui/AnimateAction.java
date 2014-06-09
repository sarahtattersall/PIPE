package pipe.actions.gui;

/**
 * Abstract class for the set of actions used during Petri net simulation
 */
public abstract class AnimateAction extends GuiAction {
    /**
     * Constructor
     * @param name animation action name
     * @param tooltip string that will appear when the action is hovered over
     * @param keystroke keyboard shortcut
     */
    public AnimateAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }
}
