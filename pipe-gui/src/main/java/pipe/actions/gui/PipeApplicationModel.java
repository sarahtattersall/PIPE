package pipe.actions.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * This class contains information about the underlying state of the application
 * For example whether it is in animation mode and what action is currently selected
 */
@SuppressWarnings("serial")
public class PipeApplicationModel implements Serializable {
    /**
     * Message fired when animation mode is toggled
     */
    public static final String TOGGLE_ANIMATION_MODE = "Toggle animation";

    /**
     * Message fired when the action type is changed on the tool bar
     */
    public static final String TYPE_ACTION_CHANGE_MESSAGE = "Type action change";

    /**
     * Property change support for publish-subscribe architecture
     */
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Zoom percentages
     */
    private final String[] zoomExamples =
            new String[]{"40%", "60%", "80%", "100%", "120%", "140%", "160%", "180%", "200%", "300%"};

    /**
     * Application name
     */
    private final String name;

    /**
     * True if edition to the Petri net is allowed
     */
    private boolean editionAllowed = true;

    /**
     * Legacy mode selected
     */
    private int mode;

    /**
     * Legacy old mode
     */
    private int oldMode;

    /**
     * Determines if PIPE is viewing in animation mode or not
     */
    private boolean inAnimationMode;

    /**
     * Type that is currently selected on the petrinet
     */
    private CreateAction selectedType;

    /**
     * Constructor
     * @param version e.g. 5
     */
    public PipeApplicationModel(String version) {
        name = "PIPE: Platform Independent Petri Net Editor " + version;
    }

    /**
     * Adds a listener for changes in this model.
     * @param listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a listener from this model
     * @param listener to remove 
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     *
     * @return true if the net is in animation mode
     */
    public boolean isInAnimationMode() {
        return inAnimationMode;
    }

    /**
     * Set whether the Petri net should be in animation mode or not
     * @param inAnimationMode true for animation mode, false for edit mode
     */
    public void setInAnimationMode(boolean inAnimationMode) {
        boolean old = this.inAnimationMode;
        this.inAnimationMode = inAnimationMode;
        if (old != inAnimationMode) {
            changeSupport.firePropertyChange(TOGGLE_ANIMATION_MODE, old, inAnimationMode);
        }
    }

    /**
     *
     * @return zoom percentages to be displayed
     */
    public String[] getZoomExamples() {
        return zoomExamples;
    }

    /**
     *
     * @return name of the application
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return true if edition is allowed
     */
    public boolean isEditionAllowed() {
        return editionAllowed;
    }

    /**
     *
     * Set whether changes are allowed to be made to the Petri net at the given time
     *
     * @param flag true if edition is allowed, false otherwise
     */
    public void setEditionAllowed(boolean flag) {
        editionAllowed = flag;
    }


    /**
     *
     * @return current mode
     */
    public int getMode() {
        return mode;
    }

    /**
     *
     * Set the current GUIAction mode
     * @param mode for GUI actions 
     */
    public void setMode(int mode) {
        this.mode = mode; 
    }

    /**
     *
     * @param action set the currently selected action on the tool bar
     */
    public void selectTypeAction(CreateAction action) {
        CreateAction old = this.selectedType;
        selectedType = action;
        changeSupport.firePropertyChange(TYPE_ACTION_CHANGE_MESSAGE, old, selectedType);
    }

    /**
     *
     * @return the currently selected action on the tool bar
     */
    public CreateAction getSelectedAction() {
        return selectedType;
    }
}
