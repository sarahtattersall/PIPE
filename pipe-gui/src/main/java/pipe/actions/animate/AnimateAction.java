package pipe.actions.animate;

import pipe.actions.GuiAction;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class AnimateAction extends GuiAction
    {
        public AnimateAction(String name, String tooltip, String keystroke)
        {
            super(name, tooltip, keystroke);
        }

        @Override
        public abstract void actionPerformed(ActionEvent event);
    }
