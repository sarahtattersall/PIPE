package pipe.models;

import pipe.actions.*;
import pipe.actions.file.*;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.views.PipeApplicationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PipeApplicationModel implements Serializable
{
    public FileAction createAction = new CreateAction();
    public FileAction openAction = new OpenAction();
    public FileAction closeAction = new CloseAction();
    public FileAction saveAction = new SaveAction();
    public FileAction saveAsAction = new SaveAsAction();
    public FileAction printAction = new PrintAction();
    public FileAction exportPNGAction = new ExportPNGAction();
    public FileAction exportTNAction = new ExportTNAction();
    public FileAction exportPSAction = new ExportPSAction();
    public FileAction importAction = new ImportAction();

    public GuiAction exitAction = new ExitAction();

    public EditAction undoAction = new EditAction("Undo", "Undo (Ctrl-Z)", "ctrl Z");
    public EditAction redoAction = new EditAction("Redo", "Redo (Ctrl-Y)", "ctrl Y");
    public EditAction cutAction = new EditAction("Cut", "Cut (Ctrl-X)", "ctrl X");
    public EditAction copyAction = new EditAction("Copy", "Copy (Ctrl-C)", "ctrl C");
    public EditAction pasteAction = new EditAction("Paste", "Paste (Ctrl-V)", "ctrl V");

    public DeleteAction deleteAction = new DeleteAction("Delete", "Delete selection", "DELETE");

    public TypeAction selectAction = new TypeAction("Select", Constants.SELECT, "Select components", "S", true);
    public TypeAction placeAction = new TypeAction("Place", Constants.PLACE, "Add a place", "P", true);
    public TypeAction transAction = new TypeAction("Immediate transition", Constants.IMMTRANS, "Add an immediate transition", "I", true);
    public TypeAction timedtransAction = new TypeAction("Timed transition", Constants.TIMEDTRANS, "Add a timed transition", "T", true);
    public TypeAction arcAction = new TypeAction("Arc", Constants.ARC, "Add an arc", "A", true);
    public TypeAction inhibarcAction = new TypeAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H", true);
    public TypeAction annotationAction = new TypeAction("Annotation", Constants.ANNOTATION, "Add an annotation", "N", true);
    public TypeAction tokenAction = new TypeAction("Add token", Constants.ADDTOKEN, "Add a token", "ADD", true);
    public TypeAction deleteTokenAction = new TypeAction("Delete token", Constants.DELTOKEN, "Delete a token", "SUBTRACT", true);
    public TypeAction dragAction = new TypeAction("Drag", Constants.DRAG, "Drag the drawing", "D", true);
    public TypeAction rateAction = new TypeAction("Rate Parameter", Constants.RATE, "Rate Parameter", "R");

    public GridAction toggleGrid = new GridAction("Cycle grid", "Change the grid size", "G");;

    public ZoomAction zoomOutAction = new ZoomAction("Zoom out", "Zoom out by 10% ", "ctrl MINUS");
    public ZoomAction zoomInAction = new ZoomAction("Zoom in", "Zoom in by 10% ", "ctrl PLUS");

    public ZoomAction zoomAction;

    public AnimateAction startAction = new AnimateAction("Animation mode", Constants.START, "Toggle Animation Mode", "Ctrl A");
    public AnimateAction stepbackwardAction = new AnimateAction("Back", Constants.STEPBACKWARD, "Step backward a firing", "4");
    public AnimateAction stepforwardAction = new AnimateAction("Forward", Constants.STEPFORWARD, "Step forward a firing", "6");
    public AnimateAction randomAction = new AnimateAction("Random", Constants.RANDOM, "Randomly fire a transition", "5");
    public AnimateAction randomAnimateAction = new AnimateAction("Animate", Constants.ANIMATE, "Randomly fire a number of transitions", "7");

    public TokenAction specifyTokenClasses = new TokenAction();

    public GroupTransitionsAction groupTransitions = new GroupTransitionsAction();

    public UnfoldAction unfoldAction = new UnfoldAction("unfoldAction", "Unfold Petri Net", "shift ctrl U");

    public UngroupTransitionsAction ungroupTransitions = new UngroupTransitionsAction("ungroupTransitions", "Ungroup any possible transitions", "shift ctrl H");

    public ChooseTokenClassAction chooseTokenClassAction = new ChooseTokenClassAction("chooseTokenClass", "Select current token", null);



    private final String[] zoomExamples = new String[]{"40%", "60%", "80%", "100%","120%", "140%", "160%", "180%", "200%", "300%"};
    private List<ZoomAction> zoomActions = new ArrayList<ZoomAction>();
    private final String _name;
    private boolean editionAllowed = true;
    private int mode;
    private int prev_mode;
    private int old_mode;
    private PipeApplicationView _observer;
    private int newNameCounter = 0;

    public PipeApplicationModel(String version)
    {
        ApplicationSettings.register(this);
        _name = "PIPE: Platform Independent Petri Net Editor " + version;
        registerZoomActions();
    }

    private void registerZoomActions()
    {
        for(int i = 0; i < zoomExamples.length; i++)
            zoomActions.add(new ZoomAction(zoomExamples[i], "Select zoom percentage", i < 10 ? "ctrl shift " + i : ""));
    }

    public String[] getZoomExamples()
    {
        return zoomExamples;
    }

    public String getName()
    {
        return _name;
    }

    public boolean isEditionAllowed()
    {
        return editionAllowed;
    }

    public void setEditionAllowed(boolean flag)
    {
        editionAllowed = flag;
    }

    public void resetMode()
    {
        setMode(old_mode);
    }

    public void enterFastMode(int _mode)
    {
        old_mode = mode;
        setMode(_mode);
    }

    public int getOldMode()
    { // NOU-PERE
        return old_mode;
    }

    public void setMode(int _mode)
    {
        // Don't bother unless new mode is different.
        if(mode != _mode)
        {
            prev_mode = mode;
            mode = _mode;
        }
    }

    public int getMode()
    {
        return mode;
    }

    void setPreviousMode()
    {
        mode = prev_mode;
    }


    public void restoreMode()
    {
        setPreviousMode();
        placeAction.setSelected(mode == Constants.PLACE);
        transAction.setSelected(mode == Constants.IMMTRANS);
        timedtransAction.setSelected(mode == Constants.TIMEDTRANS);
        arcAction.setSelected(mode == Constants.ARC);
        inhibarcAction.setSelected(mode == Constants.INHIBARC);
        tokenAction.setSelected(mode == Constants.ADDTOKEN);
        deleteTokenAction.setSelected(mode == Constants.DELTOKEN);
        rateAction.setSelected(mode == Constants.RATE);
        selectAction.setSelected(mode == Constants.SELECT);
        annotationAction.setSelected(mode == Constants.ANNOTATION);
    }

    public void enableActions(boolean status, boolean pasteEnabled)
    {
        saveAction.setEnabled(status);
        saveAsAction.setEnabled(status);

        placeAction.setEnabled(status);
        arcAction.setEnabled(status);
        inhibarcAction.setEnabled(status);
        annotationAction.setEnabled(status);
        transAction.setEnabled(status);
        timedtransAction.setEnabled(status);
        tokenAction.setEnabled(status);
        deleteAction.setEnabled(status);
        selectAction.setEnabled(status);
        deleteTokenAction.setEnabled(status);
        rateAction.setEnabled(status);
        //toggleGrid.setEnabled(status);

        if(status)
        {
            startAction.setSelected(false);
            randomAnimateAction.setSelected(false);
            stepbackwardAction.setEnabled(!status);
            stepforwardAction.setEnabled(!status);
        }
        randomAction.setEnabled(!status);
        randomAnimateAction.setEnabled(!status);

        if(!status)
        {
            pasteAction.setEnabled(status);
            undoAction.setEnabled(status);
            redoAction.setEnabled(status);
        }
        else
        {
            pasteAction.setEnabled(pasteEnabled);
        }
        copyAction.setEnabled(status);
        cutAction.setEnabled(status);
        deleteAction.setEnabled(status);

        _observer.enableActions(status);
    }

    public void registerObserver(PipeApplicationView observer)
    {
        _observer = observer;
    }

    public List<ZoomAction> getZoomActions()
    {
        return zoomActions;
    }

    public void setUndoActionEnabled(boolean flag)
    {
        undoAction.setEnabled(flag);
    }

    public void setRedoActionEnabled(boolean flag)
    {
        redoAction.setEnabled(flag);
    }

    public int newPetriNetNumber()
    {
        return ++newNameCounter;
    }
}
