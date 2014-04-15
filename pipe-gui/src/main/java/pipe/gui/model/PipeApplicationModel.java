package pipe.gui.model;

import pipe.actions.*;
import pipe.actions.gui.create.CreateAction;
import pipe.gui.ApplicationSettings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PipeApplicationModel implements Serializable {


    public static final String TOGGLE_ANIMATION_MODE = "Toggle animation";

    public static final String TYPE_ACTION_CHANGE_MESSAGE = "Type action change";

    private final String[] zoomExamples = new String[]{"40%", "60%", "80%", "100%", "120%", "140%", "160%", "180%", "200%", "300%"};
    private List<ZoomAction> zoomActions = new ArrayList<>();
    private final String _name;
    private boolean editionAllowed = true;
    private int mode;
    private int old_mode;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

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

    /**
     * Determines if PIPE is viewing in animation mode or not
     */
    private boolean inAnimationMode;

    /**
     * Type that is currently selected on the petrinet
     */
    private CreateAction selectedType;

    public PipeApplicationModel(String version) {
        ApplicationSettings.register(this);
        _name = "PIPE: Platform Independent Petri Net Editor " + version;
        registerZoomActions();
    }


    private void registerZoomActions() {
        for (int i = 0; i < zoomExamples.length; i++)
            zoomActions.add(new ZoomAction(zoomExamples[i], "Select zoom percentage", i < 10 ? "ctrl shift " + i : ""));
    }

    public String[] getZoomExamples() {
        return zoomExamples;
    }

    public String getName() {
        return _name;
    }

    public boolean isEditionAllowed() {
        return editionAllowed;
    }

    public void setEditionAllowed(boolean flag) {
        editionAllowed = flag;
    }

    public void resetMode() {
        setMode(old_mode);
    }

    public void enterFastMode(int _mode) {
        old_mode = mode;
        setMode(_mode);
    }

    public void setMode(int _mode) {

        // Don't bother unless new mode is different.
        if (mode != _mode) {
            mode = _mode;
        }
    }

    public int getMode() {
        return mode;
    }


    public List<ZoomAction> getZoomActions() {
        return zoomActions;
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
