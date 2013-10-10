/*
 * Created on 07-Mar-2004
 */
package pipe.actions;

import pipe.gui.ApplicationSettings;

import javax.swing.*;
import java.net.URL;


/**
 * GuiAction class
 *
 * @author Maxim and others
 *         <p/>
 *         Handles loading icon based on action name and setting up other stuff
 *         <p/>
 *         Toggleable actions store the toggle state in a way that allows ChangeListeners
 *         to be notified of changes
 */
public abstract class GuiAction
        extends AbstractAction
{

    protected GuiAction(String name, String tooltip, String keystroke)
    {

        super(name);
        ApplicationSettings applicationSettings = new ApplicationSettings();
        URL iconURL = Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + name + ".png");
        if(iconURL != null)
        {
            putValue(SMALL_ICON, new ImageIcon(iconURL));
        }

        if(tooltip != null)
        {
            putValue(SHORT_DESCRIPTION, tooltip);
        }

        if(keystroke != null)
        {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keystroke));
        }
    }


    protected GuiAction(String name, String tooltip, String keystroke,
                        boolean toggleable)
    {
        this(name, tooltip, keystroke);
        putValue("selected", Boolean.FALSE);
    }


    public boolean isSelected()
    {
        Boolean b = (Boolean) getValue("selected");

        if(b != null)
        {
            return b.booleanValue();
        }
        return false; // default
    }


    public void setSelected(boolean selected)
    {
        Boolean b = (Boolean) getValue("selected");

        if(b != null)
        {
            putValue("selected", null);
            putValue("selected", Boolean.valueOf(selected));
        }
    }

}
