package pipe.actions.gui.animate;

import pipe.actions.gui.GuiAction;

import java.awt.event.ActionEvent;

public abstract class AnimateAction extends GuiAction {
    public AnimateAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public abstract void actionPerformed(ActionEvent event);
}
