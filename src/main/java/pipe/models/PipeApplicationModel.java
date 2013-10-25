package pipe.models;

import pipe.actions.*;
import pipe.actions.file.*;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.views.PipeApplicationView;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeApplicationModel implements Serializable
{
    @ApplicationAction(ActionEnum.CREATE)
    public FileAction createAction = new CreateAction();

    @ApplicationAction(ActionEnum.OPEN)
    public FileAction openAction = new OpenAction();

    @ApplicationAction(ActionEnum.CLOSE)
    public FileAction closeAction = new CloseAction();

    @ApplicationAction(ActionEnum.SAVE)
    public FileAction saveAction = new SaveAction();

    @ApplicationAction(ActionEnum.SAVEAS)
    public FileAction saveAsAction = new SaveAsAction();

    @ApplicationAction(ActionEnum.PRINT)
    public FileAction printAction = new PrintAction();

    @ApplicationAction(ActionEnum.EXPORTPNG)
    public FileAction exportPNGAction = new ExportPNGAction();

    @ApplicationAction(ActionEnum.EXPORTTN)
    public FileAction exportTNAction = new ExportTNAction();

    @ApplicationAction(ActionEnum.EXPORTPS)
    public FileAction exportPSAction = new ExportPSAction();

    @ApplicationAction(ActionEnum.IMPORT)
    public FileAction importAction = new ImportAction();


    @ApplicationAction(ActionEnum.EXIT)
    public GuiAction exitAction = new ExitAction();

    @ApplicationAction(ActionEnum.UNDO)
    public EditAction undoAction = new EditAction("Undo", "Undo (Ctrl-Z)", "ctrl Z");

    @ApplicationAction(ActionEnum.REDO)
    public EditAction redoAction = new EditAction("Redo", "Redo (Ctrl-Y)", "ctrl Y");

    @ApplicationAction(ActionEnum.CUT)
    public EditAction cutAction = new EditAction("Cut", "Cut (Ctrl-X)", "ctrl X");

    @ApplicationAction(ActionEnum.COPY)
    public EditAction copyAction = new EditAction("Copy", "Copy (Ctrl-C)", "ctrl C");

    @ApplicationAction(ActionEnum.PASTE)
    public EditAction pasteAction = new EditAction("Paste", "Paste (Ctrl-V)", "ctrl V");

    @ApplicationAction(ActionEnum.DELETE)
    public DeleteAction deleteAction = new DeleteAction("Delete", "Delete selection", "DELETE");


    @ApplicationAction(ActionEnum.SELECT)
    public TypeAction selectAction = new TypeAction("Select", Constants.SELECT, "Select components", "S", true);

    @ApplicationAction(ActionEnum.PLACE)
    public TypeAction placeAction = new TypeAction("Place", Constants.PLACE, "Add a place", "P", true);

    @ApplicationAction(ActionEnum.TRANSACTION)
    public TypeAction transAction = new TypeAction("Immediate transition", Constants.IMMTRANS, "Add an immediate transition", "I", true);

    @ApplicationAction(ActionEnum.TIMED_TRANSACTION)
    public TypeAction timedtransAction = new TypeAction("Timed transition", Constants.TIMEDTRANS, "Add a timed transition", "T", true);

    @ApplicationAction(ActionEnum.ARC)
    public TypeAction arcAction = new TypeAction("Arc", Constants.ARC, "Add an arc", "A", true);

    @ApplicationAction(ActionEnum.INHIBITOR_ARC)
    public TypeAction inhibarcAction = new TypeAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H", true);

    @ApplicationAction(ActionEnum.ANNOTATION)
    public TypeAction annotationAction = new TypeAction("Annotation", Constants.ANNOTATION, "Add an annotation", "N", true);

    @ApplicationAction(ActionEnum.TOKEN)
    public TypeAction tokenAction = new TypeAction("Add token", Constants.ADDTOKEN, "Add a token", "ADD", true);

    @ApplicationAction(ActionEnum.DELETE_TOKEN)
    public TypeAction deleteTokenAction = new TypeAction("Delete token", Constants.DELTOKEN, "Delete a token", "SUBTRACT", true);

    @ApplicationAction(ActionEnum.DRAG)
    public TypeAction dragAction = new TypeAction("Drag", Constants.DRAG, "Drag the drawing", "D", true);

    @ApplicationAction(ActionEnum.RATE_PARAMETER)
    public TypeAction rateAction = new TypeAction("Rate Parameter", Constants.RATE, "Rate Parameter", "R");

    @ApplicationAction(ActionEnum.TOGGLE_GRID)
    public GridAction toggleGrid = new GridAction("Cycle grid", "Change the grid size", "G");;


    @ApplicationAction(ActionEnum.ZOOM_OUT)
    public ZoomAction zoomOutAction = new ZoomAction("Zoom out", "Zoom out by 10% ", "ctrl MINUS");

    @ApplicationAction(ActionEnum.ZOOM_IN)
    public ZoomAction zoomInAction = new ZoomAction("Zoom in", "Zoom in by 10% ", "ctrl PLUS");

    public ZoomAction zoomAction;


    @ApplicationAction(ActionEnum.START)
    public AnimateAction startAction = new AnimateAction("Animation mode", Constants.START, "Toggle Animation Mode", "Ctrl A");

    @ApplicationAction(ActionEnum.STEP_BACK)
    public AnimateAction stepbackwardAction = new AnimateAction("Back", Constants.STEPBACKWARD, "Step backward a firing", "4");


    @ApplicationAction(ActionEnum.STEP_FORWARD)
    public AnimateAction stepforwardAction = new AnimateAction("Forward", Constants.STEPFORWARD, "Step forward a firing", "6");

    @ApplicationAction(ActionEnum.RANDOM)
    public AnimateAction randomAction = new AnimateAction("Random", Constants.RANDOM, "Randomly fire a transition", "5");

    @ApplicationAction(ActionEnum.ANIMATE)
    public AnimateAction randomAnimateAction = new AnimateAction("Animate", Constants.ANIMATE, "Randomly fire a number of transitions", "7");

    @ApplicationAction(ActionEnum.SPECIFY_TOKEN)
    public TokenAction specifyTokenClasses = new TokenAction();

    @ApplicationAction(ActionEnum.GROUP_TRANSITIONS)
    public GroupTransitionsAction groupTransitions = new GroupTransitionsAction();

    @ApplicationAction(ActionEnum.UNFOLD)
    public UnfoldAction unfoldAction = new UnfoldAction("unfoldAction", "Unfold Petri Net", "shift ctrl U");

    @ApplicationAction(ActionEnum.UNGROUP_TRANSITIONS)
    public UngroupTransitionsAction ungroupTransitions = new UngroupTransitionsAction("ungroupTransitions", "Ungroup any possible transitions", "shift ctrl H");

    @ApplicationAction(ActionEnum.CHOOSE_TOKEN_CLASS)
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

    Map<ActionEnum, GuiAction> actionMap = new HashMap<ActionEnum, GuiAction>();

    public PipeApplicationModel(String version)
    {
        ApplicationSettings.register(this);
        _name = "PIPE: Platform Independent Petri Net Editor " + version;
        registerZoomActions();
        populateActionMap();
    }

    private void populateActionMap()
    {
        for (Field field : this.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(ApplicationAction.class)) {
                ApplicationAction annotation = field.getAnnotation(ApplicationAction.class);
                if (GuiAction.class.isAssignableFrom(field.getType()))
                {
                    try {
                        GuiAction action = (GuiAction) field.get(this);
                        actionMap.put(annotation.value(), action);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public GuiAction getAction(ActionEnum type)
    {
        return actionMap.get(type);
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
