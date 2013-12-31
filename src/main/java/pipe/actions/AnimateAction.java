package pipe.actions;

import pipe.gui.*;
import pipe.models.PipeApplicationModel;
import pipe.views.PetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class AnimateAction extends GuiAction
    {
        public AnimateAction(String name, int typeID, String tooltip, String keystroke)
        {
            super(name, tooltip, keystroke);
        }

        public abstract void actionPerformed(ActionEvent event);
    }
