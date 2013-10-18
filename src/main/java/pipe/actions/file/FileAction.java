package pipe.actions.file;

import pipe.actions.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;
import pipe.gui.widgets.FileBrowser;
import pipe.models.PipeApplicationModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public abstract class FileAction extends GuiAction
{
    public FileAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public abstract void actionPerformed(ActionEvent e);

}
