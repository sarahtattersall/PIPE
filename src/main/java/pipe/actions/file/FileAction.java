package pipe.actions.file;

import pipe.actions.GuiAction;

import java.awt.event.ActionEvent;

public abstract class FileAction extends GuiAction
{
    public FileAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public abstract void actionPerformed(ActionEvent e);

}
