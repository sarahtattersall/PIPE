package pipe.models;

import pipe.actions.*;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;

public class PipeApplicationModel implements Serializable
{
    public FileAction createAction;
    public FileAction openAction;
    public FileAction closeAction;
    public FileAction saveAction;
    public FileAction saveAsAction;
    public FileAction exitAction;
    public FileAction printAction;
    public FileAction exportPNGAction;
    public FileAction exportTNAction;
    public FileAction exportPSAction;
    public FileAction importAction;

    public EditAction copyAction;
    public EditAction cutAction;
    public EditAction pasteAction;
    public EditAction undoAction;
    public EditAction redoAction;
    public GridAction toggleGrid;
    public ZoomAction zoomOutAction, zoomInAction, zoomAction;
    public DeleteAction deleteAction;
    public TypeAction annotationAction;
    public TypeAction arcAction;
    public TypeAction inhibarcAction;
    public TypeAction placeAction;
    public TypeAction transAction;
    public TypeAction timedtransAction;
    public TypeAction tokenAction;
    public TypeAction selectAction;
    public TypeAction rateAction;
    public TypeAction deleteTokenAction;
    public TypeAction dragAction;
    public AnimateAction startAction;
    public AnimateAction stepforwardAction;
    public AnimateAction stepbackwardAction;
    public AnimateAction randomAction;
    public AnimateAction randomAnimateAction;
    public TokenAction _specifyTokenClasses;
    public GroupTransitionsAction groupTransitions;
    public UnfoldAction unfoldAction;
    public UngroupTransitionsAction ungroupTransitions;
    public ChooseTokenClassAction chooseTokenClassAction;
    private ArrayList<ZoomAction> _zoomActions;

    private final String _name;
    private final String[] _zoomExamples;
    private boolean _editionAllowed;
    private int mode, prev_mode, old_mode;
    private PipeApplicationView _observer;

    private int newNameCounter;

    public PipeApplicationModel(String version)
    {
        ApplicationSettings.register(this);

        _name = "PIPE: Platform Independent Petri Net Editor " + version;
        _zoomExamples = new String[]{"40%", "60%", "80%", "100%","120%", "140%", "160%", "180%", "200%", "300%"};
        _editionAllowed = true;
        boolean dragging = false;
        newNameCounter = 0;
        registerActions();
        ArrayList<PetriNet> petriNets = new ArrayList<PetriNet>();

    }

    private void registerActions()
    {
        createAction = new FileAction("New", "Create a new Petri net", "ctrl N");
        openAction = new FileAction("Open", "Open", "ctrl O");
        closeAction = new FileAction("Close", "Close the current tab", "ctrl W");
        saveAction = new FileAction("Save", "Save", "ctrl S");
        saveAsAction = new FileAction("Save as", "Save as...", "shift ctrl S");
        importAction = new FileAction("Import", "Import from eDSPN", "ctrl I");
        exportPNGAction = new FileAction("PNG", "Export the net to PNG format", "ctrl G");
        exportPSAction = new FileAction("PostScript", "Export the net to PostScript format", "ctrl T");
        exportTNAction = new FileAction("eDSPN", "Export the net to Timenet format", "ctrl E");
        printAction = new FileAction("Print", "Print", "ctrl P");
        undoAction = new EditAction("Undo", "Undo (Ctrl-Z)", "ctrl Z");
        redoAction = new EditAction("Redo", "Redo (Ctrl-Y)", "ctrl Y");
        cutAction = new EditAction("Cut", "Cut (Ctrl-X)", "ctrl X");
        copyAction = new EditAction("Copy", "Copy (Ctrl-C)", "ctrl C");
        pasteAction = new EditAction("Paste", "Paste (Ctrl-V)", "ctrl V");
        deleteAction = new DeleteAction("Delete", "Delete selection", "DELETE");
        selectAction = new TypeAction("Select", Constants.SELECT, "Select components", "S", true);

        Action actionListener = new AbstractAction()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(isEditionAllowed())
                {
                    selectAction.actionPerformed(null);
                }
            }
        };

        placeAction = new TypeAction("Place", Constants.PLACE, "Add a place", "P", true);
        transAction = new TypeAction("Immediate transition", Constants.IMMTRANS, "Add an immediate transition", "I", true);
        timedtransAction = new TypeAction("Timed transition", Constants.TIMEDTRANS, "Add a timed transition", "T", true);
        arcAction = new TypeAction("Arc", Constants.ARC, "Add an arc", "A", true);
        inhibarcAction = new TypeAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H", true);
        annotationAction = new TypeAction("Annotation", Constants.ANNOTATION, "Add an annotation", "N", true);
        tokenAction = new TypeAction("Add token", Constants.ADDTOKEN, "Add a token", "ADD", true);
        deleteTokenAction = new TypeAction("Delete token", Constants.DELTOKEN, "Delete a token", "SUBTRACT", true);
        _specifyTokenClasses = new TokenAction();
        groupTransitions = new GroupTransitionsAction();
        ungroupTransitions = new UngroupTransitionsAction("ungroupTransitions", "Ungroup any possible transitions", "shift ctrl H");
        unfoldAction = new UnfoldAction("unfoldAction", "Unfold Petri Net", "shift ctrl U");
        rateAction = new TypeAction("Rate Parameter", Constants.RATE, "Rate Parameter", "R");
        zoomOutAction = new ZoomAction("Zoom out", "Zoom out by 10% ", "ctrl MINUS");
        zoomInAction = new ZoomAction("Zoom in", "Zoom in by 10% ", "ctrl PLUS");
        toggleGrid = new GridAction("Cycle grid", "Change the grid size", "G");
        dragAction = new TypeAction("Drag", Constants.DRAG, "Drag the drawing", "D", true);
        startAction = new AnimateAction("Animation mode", Constants.START, "Toggle Animation Mode", "Ctrl A");
        stepbackwardAction = new AnimateAction("Back", Constants.STEPBACKWARD, "Step backward a firing", "4");
        stepforwardAction = new AnimateAction("Forward", Constants.STEPFORWARD, "Step forward a firing", "6");
        randomAction = new AnimateAction("Random", Constants.RANDOM, "Randomly fire a transition", "5");
        randomAnimateAction = new AnimateAction("Animate", Constants.ANIMATE, "Randomly fire a number of transitions", "7");
        chooseTokenClassAction = new ChooseTokenClassAction("chooseTokenClass", "Select current token", null);
        exitAction = new FileAction("Exit", "Close the program", "ctrl Q");

        _zoomActions = new ArrayList<ZoomAction>();
        for(int i=0; i<_zoomExamples.length; i++)
            _zoomActions.add(new ZoomAction(_zoomExamples[i], "Select zoom percentage", i < 10 ? "ctrl shift " + i : ""));
    }

    public String[] getZoomExamples()
    {
        return _zoomExamples;
    }

    public String getName()
    {
        return _name;
    }

    public boolean isEditionAllowed()
    {
        return _editionAllowed;
    }

    public void setEditionAllowed(boolean flag)
    {
        _editionAllowed = flag;
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

    public ArrayList<ZoomAction> getZoomActions()
    {
        return _zoomActions;
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
