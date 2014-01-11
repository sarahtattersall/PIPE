package pipe.gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ToggleButton extends JToggleButton implements PropertyChangeListener
{
    public ToggleButton(Action a)
    {
        super(a);
        if(a.getValue(Action.SMALL_ICON) != null)
            setText(null);
        a.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        if(evt.getPropertyName().equals("selected"))
        {
            Boolean b = (Boolean) evt.getNewValue();
            if(b != null)
                setSelected(b.booleanValue());
        }
    }

}
