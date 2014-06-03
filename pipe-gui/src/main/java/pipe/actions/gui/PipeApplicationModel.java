package pipe.actions.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * This class contains information about the underlying state of the application
 * For example whether it is in animation mode and what action is currently selected
 */
public class PipeApplicationModel implements Serializable {


    public static final String TOGGLE_ANIMATION_MODE = "Toggle animation";

    public static final String TYPE_ACTION_CHANGE_MESSAGE = "Type action change";

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private final String[] zoomExamples =
            new String[]{"40%", "60%", "80%", "100%", "120%", "140%", "160%", "180%", "200%", "300%"};

    private final String name;

    private boolean editionAllowed = true;

    private int mode;

    private int oldMode;

    /**
     * Determines if PIPE is viewing in animation mode or not
     */
    private boolean inAnimationMode;

    /**
     * Type that is currently selected on the petrinet
     */
    private CreateAction selectedType;

    public PipeApplicationModel(String version) {
        name = "PIPE: Platform Independent Petri Net Editor " + version;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public boolean isInAnimationMode() {
        return inAnimationMode;
    }

    public void setInAnimationMode(boolean inAnimationMode) {
        boolean old = this.inAnimationMode;
        this.inAnimationMode = inAnimationMode;
        if (old != inAnimationMode) {
            changeSupport.firePropertyChange(TOGGLE_ANIMATION_MODE, old, inAnimationMode);
        }
    }

    public String[] getZoomExamples() {
        return zoomExamples;
    }

    public String getName() {
        return name;
    }

    public boolean isEditionAllowed() {
        return editionAllowed;
    }

    public void setEditionAllowed(boolean flag) {
        editionAllowed = flag;
    }

    public void resetMode() {
        setMode(oldMode);
    }

    public void enterFastMode(int _mode) {
        oldMode = mode;
        setMode(_mode);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int _mode) {

        // Don't bother unless new mode is different.
        if (mode != _mode) {
            mode = _mode;
        }
    }

    public void selectTypeAction(CreateAction action) {
        CreateAction old = this.selectedType;
        selectedType = action;
        changeSupport.firePropertyChange(TYPE_ACTION_CHANGE_MESSAGE, old, selectedType);
    }

    public CreateAction getSelectedAction() {
        return selectedType;
    }
}
