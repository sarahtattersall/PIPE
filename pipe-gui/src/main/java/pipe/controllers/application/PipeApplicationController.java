package pipe.controllers.application;

import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.*;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.IncludeHierarchyTreePanel;
import pipe.historyActions.AnimationHistoryImpl;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.animation.PetriNetAnimator;
import uk.ac.imperial.pipe.exceptions.IncludeException;
import uk.ac.imperial.pipe.io.PetriNetFileException;
import uk.ac.imperial.pipe.models.manager.PetriNetManager;
import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.event.UndoableEditListener;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Pipes main application controller.
 * It houses the Petri net controllers of open tabs and is responsible for the creation of Petri nets
 */
public class PipeApplicationController implements PropertyChangeListener  {

    public static final String KEEP_ROOT_TAB_ACTIVE_MESSAGE = "Keep tab for root include hierarchy active";

	public static final String NEW_ACTIVE_INCLUDE_HIERARCHY = "New include hierarchy now active";

	public static final String SWITCH_TAB_FOR_NEW_ACTIVE_INCLUDE_HIERARCHY = "Switch tab for new active include hierarchy";


	/**
     * Controllers for each tab
     */
    private final Map<PetriNetTab, PetriNetController> netControllers = new HashMap<>();
    /**
     * Tabs for each include
     */
    private final Map<IncludeHierarchy, PetriNetTab> includeTabs = new HashMap<>();

    /**
     * Main PIPE application model
     */
    private final PipeApplicationModel applicationModel;

    /**
     * Manages creation/deletion of Petri net models
     */
    private final PetriNetManager manager = new PetriNetManagerImpl();

    /**
     * List of separate root-level include hierarchies being used by the application
     */
    private final List<IncludeHierarchy> rootIncludes = new ArrayList<>();

    /**
     * The current tab displayed in the view
     */
    private PetriNetTab activeTab;

    /**
     * The current include hierarchy being processed 
     * (whether root level or any of its children) 
     */
	private IncludeHierarchy activeIncludeHierarchy;

	/**
	 * Count of includes and associated tabs that are still expected to be registered.   
	 */
	protected int expectedIncludeCount = 0;

    /**
     * Support notification of listener (PipeApplicationView) of changes in the application
     */
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Constructor
     * @param applicationModel Main PIPE application model
     */
    public PipeApplicationController(PipeApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        manager.addPropertyChangeListener(this); 
    }

    /**
     *
     * @param listener to listen for change events in the petri net manager
     */
    public void registerToManager(PropertyChangeListener listener) {
        manager.addPropertyChangeListener(listener);
    }
    /**
    *
    * @param listener listens for changes on the Petri net
    */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }
    /**
    *
    * @param listener current listener listining to the Petri net
    */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Creates an empty Petri net with a default token
     */
    public void createEmptyPetriNet() {
        manager.createNewPetriNet();
    }


    /**
     * Register the tab to the Petri net
     * @param net Petri net
     * @param tab tab which houses the graphical petri net components
     * @param historyObserver listener for stepback/forward events in animation
     * @param undoListener listener for undo/redo events
     * @param zoomListener listener for zoom events
     * @return controller for the Petri net associated with this tab
     */
    //TODO: THIS IS RATHER UGLY, too many params but better than what was here before
    public PetriNetController registerTab(PetriNet net, PetriNetTab tab, Observer historyObserver, UndoableEditListener undoListener,
                            PropertyChangeListener zoomListener) {
        GUIAnimator animator = buildAnimatorWithHistory(net, historyObserver);

        CopyPasteManager copyPasteManager = buildCopyPasteManager(net, tab,
				undoListener);

        ZoomController zoomController = buildZoomController(tab);
        PetriNetController petriNetController =
                new PetriNetController(net, undoListener, animator, copyPasteManager, zoomController, tab);
        netControllers.put(tab, petriNetController);

        PropertyChangeListener changeListener =
                new PetriNetComponentChangeListener(applicationModel, tab, petriNetController);
        net.addPropertyChangeListener(changeListener);
        mapTabToInclude(tab);
        setActiveTab(tab);
        initialiseNet(net, changeListener);
        if (areIncludeAdditionsPending()) {
        	expectedIncludeCount--;
        }
        return petriNetController; 
    }

	protected ZoomController buildZoomController(PetriNetTab tab) {
		ZoomController zoomController = new ZoomController(100);
        tab.addZoomListener(zoomController);
		return zoomController;
	}

	protected CopyPasteManager buildCopyPasteManager(PetriNet net,
			PetriNetTab tab, UndoableEditListener undoListener) {
		CopyPasteManager copyPasteManager = new CopyPasteManager(undoListener, tab, net, this);
		return copyPasteManager;
	}

	protected GUIAnimator buildAnimatorWithHistory(PetriNet net,
			Observer historyObserver) {
		AnimationHistoryImpl animationHistory = new AnimationHistoryImpl();
        animationHistory.addObserver(historyObserver);
        GUIAnimator animator = new GUIAnimator(new PetriNetAnimator(net.getExecutablePetriNet()), animationHistory, this);
		return animator;
	}

    /**
     * Determines which tab will be displayed in the view.  Called under two circumstances:
     * <ul>
     * <li>User clicks on a particular tab in the view
     * <li>As each of one or more Petri nets is being loaded.  If multiple Petri nets being loaded, 
     * as part of loading an IncludeHierarchy, the active tab will be that of the Petri net corresponding to the 
     * root level of the IncludeHierarchy
     * </ul> 
     * @param tab to potentially be made active in the view 
     */
    public void setActiveTab(PetriNetTab tab) {
    	if (areIncludeAdditionsPending()) {
    		IncludeHierarchy tabInclude = getInclude(tab);
    		if (activeIncludeHierarchy.equals(tabInclude)) {
    			this.activeTab = tab;
    			changeSupport.firePropertyChange(KEEP_ROOT_TAB_ACTIVE_MESSAGE, null, activeTab);
    		}
    	} else {
    		this.activeTab = tab;
    		setActiveIncludeHierarchyAndNotifyTreePanel(getInclude(activeTab));
    	}
    }

	private IncludeHierarchy getInclude(PetriNetTab tab) {
		return netControllers.get(tab).getPetriNet().getIncludeHierarchy();
	}

	protected void mapTabToInclude(PetriNetTab tab) {
		IncludeHierarchy tabInclude = getPetriNetController(tab).getPetriNet().getIncludeHierarchy();
		includeTabs.put(tabInclude, tab);
	}

    /**
     * This is a little hacky, I'm not sure how to make this better when it's so late
     * If a better implementation is clear please re-write
     * <p>
     * This method invokes the change listener which will create the view objects on the
     * petri net tab
     * </p>
     * @param propertyChangeListener listener for changes to the net
     * @param net Petri net to be created 
     */
    //TODO move to PetriNet:  addListenerForAllComponents(PropertyChangeListener propertyChangeListener)
    protected void initialiseNet(PetriNet net, PropertyChangeListener propertyChangeListener) {
        for (Token token : net.getTokens()) {
            PropertyChangeEvent changeEvent =
                    new PropertyChangeEvent(net, PetriNet.NEW_TOKEN_CHANGE_MESSAGE, null, token);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Place place : net.getPlaces()) {
            PropertyChangeEvent changeEvent =
                    new PropertyChangeEvent(net, PetriNet.NEW_PLACE_CHANGE_MESSAGE, null, place);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Transition transition : net.getTransitions()) {
            PropertyChangeEvent changeEvent =
                    new PropertyChangeEvent(net, PetriNet.NEW_TRANSITION_CHANGE_MESSAGE, null, transition);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Arc<? extends Connectable, ? extends Connectable> arc : net.getArcs()) {
            PropertyChangeEvent changeEvent = new PropertyChangeEvent(net, PetriNet.NEW_ARC_CHANGE_MESSAGE, null, arc);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Annotation annotation : net.getAnnotations()) {
            PropertyChangeEvent changeEvent =
                    new PropertyChangeEvent(net, PetriNet.NEW_ANNOTATION_CHANGE_MESSAGE, null, annotation);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (RateParameter rateParameter : net.getRateParameters()) {
            PropertyChangeEvent changeEvent =
                    new PropertyChangeEvent(net, PetriNet.NEW_RATE_PARAMETER_CHANGE_MESSAGE, null, rateParameter);
            propertyChangeListener.propertyChange(changeEvent);
        }
    }

    /**
     * Loads and creates a Petri net located at the given file
     * @param file location of the XML file which contains a PNML representation of a Petri net
     * @throws UnparsableException if the file cannot be parsed or a rate parameter expression cannot be parsed 
     * @throws PetriNetFileException if the file does not exist, or is not valid XML, 
     * or whose highest level tags are not <code>pnml</code> or <code>include</code>   
     * @throws IncludeException if errors are encountered building an include hierarchy 
     * @throws FileNotFoundException if one of the referenced files does not exist 
     */
    public void createNewTabFromFile(File file) throws UnparsableException, PetriNetFileException, FileNotFoundException, IncludeException {
        try {
            manager.createFromFile(file);
        } catch (JAXBException e) {
            throw new UnparsableException("Could not initialise Petri net reader!", e);
        }
    }

    /**
     * Save the currently displayed petri net to the specified file
     * @param outFile location to save the Petri net
     * @throws ParserConfigurationException configuration error 
     * @throws TransformerException transformer error 
     * @throws IllegalAccessException illegal access
     * @throws NoSuchMethodException method not found 
     * @throws InvocationTargetException invocation error 
     */
    public void saveAsCurrentPetriNet(File outFile)
            throws ParserConfigurationException, TransformerException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        PetriNetController petriNetController = getActivePetriNetController();
        PetriNet petriNet = petriNetController.getPetriNet();

        try {
            manager.savePetriNet(petriNet, outFile);
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Failed to write!", e);
        }
        petriNetController.save();
    }

    /**
     *
     * @return the active Petri net controller
     */
    public PetriNetController getActivePetriNetController() {
        return netControllers.get(activeTab);
    }

    /**
     * @return true if the current petri net has changed
     */
    public boolean hasCurrentPetriNetChanged() {
        PetriNetController activeController = getActivePetriNetController();
        return activeController != null && activeController.hasChanged();
    }

    public boolean anyNetsChanged() {
        return !getNetsChanged().isEmpty();
    }

    /**
     * @return the names of the petri nets that have changed
     */
    public Set<String> getNetsChanged() {
        Set<String> changed = new HashSet<>();
        for (PetriNetController controller : netControllers.values()) {
            if (controller.hasChanged()) {
                changed.add(controller.getPetriNet().getNameValue());
            }
        }
        return changed;
    }

    /**
     * Removes the active tab from display if it exists.
     * Note active tab must be removed from netControllers before the petri net is removed
     * from the manager because the manager will fire a message which causes the active tab
     * to be swapped to the new open tab
     */
    public void removeActiveTab() {
        if (activeTab != null) {
            PetriNetController controller = netControllers.get(activeTab);
            netControllers.remove(activeTab);
            PetriNet petriNet = controller.getPetriNet();
            manager.remove(petriNet);
        }
    }

    /**
     *
     * @return the current active tab
     */
    public PetriNetTab getActiveTab() {
        return activeTab;
    }

    /**
     * fires when a new include hierarchy has been created.  The hierarchy will be added to the list 
     * of root includes
     * @param event that a new root level include message has been received 
     */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PetriNetManagerImpl.NEW_ROOT_LEVEL_INCLUDE_HIERARCHY_MESSAGE)) {
			addIncludeHierarchy((IncludeHierarchy) event.getNewValue()); 
		}
		
	}

	private void addIncludeHierarchy(IncludeHierarchy include) {
		rootIncludes.add(include);
		activeIncludeHierarchy = include;
		expectedIncludeCount = include.getMap(IncludeHierarchyMapEnum.INCLUDE_ALL).size(); 
	}

	public IncludeHierarchy getActiveIncludeHierarchy() {
		return activeIncludeHierarchy;
	}
	/**
	 * Set which level of the include hierarchy is currently active -- may be the root level or any of the children.
	 * Called either by {@link #setActiveIncludeHierarchyAndNotifyTreePanel(IncludeHierarchy)} or 
	 * {@link #setActiveIncludeHierarchyAndNotifyView(IncludeHierarchy)} 
	 * @param include to be made active
	 */
	protected void setActiveIncludeHierarchy(IncludeHierarchy include) {
		activeIncludeHierarchy = include;
	}
	/**
	 * Set which level of the include hierarchy is currently active -- may be the root level or any of the children.
	 * Called by selecting the corresponding tab in the GUI.  
	 * Updates include hierarchy tree panel listener whenever the active include hierarchy changes 
	 * @see IncludeHierarchyTreePanel 
	 * @param include to be made active
	 */
	public void setActiveIncludeHierarchyAndNotifyTreePanel(IncludeHierarchy include) {
		if (!include.equals(activeIncludeHierarchy)) {
			setActiveIncludeHierarchy(include);
			changeSupport.firePropertyChange(NEW_ACTIVE_INCLUDE_HIERARCHY, null, include);
		}
	}
	/**
	 * Set which level of the include hierarchy is currently active -- may be the root level or any of the children.
	 * Called by selection of a node in the tree panel include hierarchy display 
	 * Updates PipeApplicationView listener whenever the active include hierarchy changes 
	 * @see IncludeHierarchyTreePanel 
	 * @see PipeApplicationView 
	 * @param include to be made active
	 */
	public void setActiveIncludeHierarchyAndNotifyView(IncludeHierarchy include) {
		if (!include.equals(activeIncludeHierarchy)) {
			setActiveIncludeHierarchy(include);
			changeSupport.firePropertyChange(SWITCH_TAB_FOR_NEW_ACTIVE_INCLUDE_HIERARCHY, null, getTab(include));
		}
	}

	public PetriNetTab getTab(IncludeHierarchy include) {
		return includeTabs.get(include);
	}

	/**
	 * @return whether tabs are still to be registered for root or children of activeIncludeHierarchy 
	 */
	public boolean areIncludeAdditionsPending() {
		return (expectedIncludeCount > 0);
	}
	/**
	 * @param petriNetTab for which PetriNetController is to be returned
	 * @return the PetriNetController for a given PetriNetTab
	 */
	protected PetriNetController getPetriNetController(PetriNetTab petriNetTab) {
		return netControllers.get(petriNetTab); 
	}
}
