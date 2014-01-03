package pipe.models;

import pipe.actions.*;
import pipe.actions.edit.*;
import pipe.actions.file.*;
import pipe.actions.type.*;
import pipe.actions.animate.*;
import pipe.actions.type.AddTokenAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.models.visitor.connectable.arc.InhibitorCreatorVisitor;
import pipe.models.visitor.connectable.arc.InhibitorSourceVisitor;
import pipe.models.visitor.connectable.arc.NormalArcCreatorVisitor;
import pipe.models.visitor.connectable.arc.NormalArcSourceVisitor;
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
    public GuiAction undoAction = new UndoAction();

    @ApplicationAction(ActionEnum.REDO)
    public GuiAction redoAction = new RedoAction();

    @ApplicationAction(ActionEnum.COPY)
    public GuiAction copyAction = new CopyAction("Copy", "Copy (Ctrl-C)", "ctrl C");

    @ApplicationAction(ActionEnum.CUT)
    public GuiAction cutAction = new CutAction("Cut", "Cut (Ctrl-X)", "ctrl X");

    @ApplicationAction(ActionEnum.PASTE)
    public GuiAction pasteAction = new PasteAction("Paste", "Paste (Ctrl-V)", "ctrl V");

    @ApplicationAction(ActionEnum.DELETE)
    public DeleteAction deleteAction = new DeleteAction("Delete", "Delete selection", "DELETE");


    @ApplicationAction(ActionEnum.SELECT)
    public TypeAction selectAction = new SelectAction("Select", Constants.SELECT, "Select components", "S");

    @ApplicationAction(ActionEnum.PLACE)
    public TypeAction placeAction = new PlaceAction("Place", Constants.PLACE, "Add a place", "P");

    @ApplicationAction(ActionEnum.TRANSACTION)
    public TypeAction transAction = new ImmediateTransitionAction("Immediate transition", Constants.IMMTRANS, "Add an immediate transition", "I");

    @ApplicationAction(ActionEnum.TIMED_TRANSACTION)
    public TypeAction timedtransAction = new TimedTransitionAction("Timed transition", Constants.TIMEDTRANS, "Add a timed transition", "T");

    @ApplicationAction(ActionEnum.ARC)
    public final TypeAction arcAction;

    @ApplicationAction(ActionEnum.INHIBITOR_ARC)
    public final TypeAction inhibarcAction;

    @ApplicationAction(ActionEnum.ANNOTATION)
    public TypeAction annotationAction = new AnnotationAction("Annotation", Constants.ANNOTATION, "Add an annotation", "N");

    @ApplicationAction(ActionEnum.TOKEN)
    public TypeAction tokenAction = new AddTokenAction("Add token", Constants.ADDTOKEN, "Add a token", "ADD");

    @ApplicationAction(ActionEnum.DELETE_TOKEN)
    public TypeAction deleteTokenAction = new DeleteTokenAction("Delete token", Constants.DELTOKEN, "Delete a token", "SUBTRACT");

    @ApplicationAction(ActionEnum.DRAG)
    public TypeAction dragAction = new DragAction("Drag", Constants.DRAG, "Drag the drawing", "D");

    @ApplicationAction(ActionEnum.RATE_PARAMETER)
    public TypeAction rateAction = new RateAction("Rate Parameter", Constants.RATE, "Rate Parameter", "R");

    @ApplicationAction(ActionEnum.TOGGLE_GRID)
    public GridAction toggleGrid = new GridAction("Cycle grid", "Change the grid size", "G");;


    @ApplicationAction(ActionEnum.ZOOM_OUT)
    public ZoomAction zoomOutAction = new ZoomAction("Zoom out", "Zoom out by 10% ", "ctrl MINUS");

    @ApplicationAction(ActionEnum.ZOOM_IN)
    public ZoomAction zoomInAction = new ZoomAction("Zoom in", "Zoom in by 10% ", "ctrl PLUS");

    public ZoomAction zoomAction;


    @ApplicationAction(ActionEnum.START)
    public AnimateAction startAction = new ToggleAnimateAction("Animation mode", "Toggle Animation Mode", "Ctrl A");

    @ApplicationAction(ActionEnum.STEP_BACK)
    public AnimateAction stepbackwardAction = new StepBackwardAction("Back", "Step backward a firing", "4");


    @ApplicationAction(ActionEnum.STEP_FORWARD)
    public AnimateAction stepforwardAction = new StepForwardAction("Forward", "Step forward a firing", "6");

    @ApplicationAction(ActionEnum.RANDOM)
    public AnimateAction randomAction = new RandomAnimateAction("Random", "Randomly fire a transition", "5");

    @ApplicationAction(ActionEnum.ANIMATE)
    public AnimateAction randomAnimateAction = new AnimationAction("Animate", "Randomly fire a number of transitions", "7");

    @ApplicationAction(ActionEnum.SPECIFY_TOKEN)
    public SpecifyTokenAction specifyTokenClasses = new SpecifyTokenAction();

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

    /**
     * Type that is currently selected on the petrinet
     */
    private TypeAction selectedType;

    public PipeApplicationModel(PipeApplicationController controller, String version)
    {
        inhibarcAction = new ArcAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H", new InhibitorSourceVisitor(), new InhibitorCreatorVisitor(controller));
        arcAction = new ArcAction("Arc", Constants.ARC, "Add an arc", "A", new NormalArcSourceVisitor(), new NormalArcCreatorVisitor(controller));
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


    public void selectTypeAction(TypeAction action) {
        selectedType = action;
    }

    public TypeAction getSelectedAction() {
        return selectedType;
    }
}
