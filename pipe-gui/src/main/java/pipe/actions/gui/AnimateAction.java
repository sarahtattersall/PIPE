package pipe.actions.gui;

import java.awt.event.ActionEvent;

public abstract class AnimateAction extends GuiAction {
    public AnimateAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public abstract void actionPerformed(ActionEvent event);
}
