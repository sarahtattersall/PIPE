package pipe.gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A toggleable button
 */
public class ToggleButton extends JToggleButton implements PropertyChangeListener {
    /**
     * Constructor
     *
     * @param a toggle action to perform when the button is pressed
     */
    public ToggleButton(Action a) {
        super(a);
        if (a.getValue(Action.SMALL_ICON) != null) {
            setText(null);
        }
        a.addPropertyChangeListener(this);
    }

    /**
     * Performs action on press event
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selected")) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b != null) {
                setSelected(b);
            }
        }
    }

}
