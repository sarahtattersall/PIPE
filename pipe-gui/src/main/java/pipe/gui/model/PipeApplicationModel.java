package pipe.gui.model;

import pipe.actions.*;
import pipe.gui.ApplicationSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PipeApplicationModel implements Serializable {


    private final String[] zoomExamples = new String[]{"40%", "60%", "80%", "100%", "120%", "140%", "160%", "180%", "200%", "300%"};
    private List<ZoomAction> zoomActions = new ArrayList<>();
    private final String _name;
    private boolean editionAllowed = true;
    private int mode;
    private int old_mode;
    private int newNameCounter = 0;

    /**
     * Type that is currently selected on the petrinet
     */
    private TypeAction selectedType;

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

    public int newPetriNetNumber() {
        return ++newNameCounter;
    }


    public void selectTypeAction(TypeAction action) {
        selectedType = action;
    }

    public TypeAction getSelectedAction() {
        return selectedType;
    }
}
