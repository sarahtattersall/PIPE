package pipe.controllers;

import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.SwingMouseUtilities;
import pipe.historyActions.AnimationHistory;
import pipe.views.PipeApplicationView;
import pipe.views.changeListener.PetriNetChangeListener;
import uk.ac.imperial.pipe.animation.PetriNetAnimator;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Annotation;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.RateParameter;
import uk.ac.imperial.pipe.models.petrinet.Token;
import uk.ac.imperial.pipe.models.petrinet.Transition;
import uk.ac.imperial.pipe.models.manager.PetriNetManager;
import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.event.UndoableEditListener;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PipeApplicationController {

    /**
     * Controllers for each tab
     */
    private final Map<PetriNetTab, PetriNetController> netControllers = new HashMap<>();

    /**
     * Selection managers for each tab
     */
    private final Map<PetriNetTab, SelectionManager> selectionManagers = new HashMap<>();


    private final PipeApplicationModel applicationModel;

    /**
     * Manages creation/deletion of Petri net models
     */
    private final PetriNetManager manager = new PetriNetManagerImpl();

    /**
     * The current tab displayed in the view
     */
    private PetriNetTab activeTab;

    public PipeApplicationController(PipeApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        ApplicationSettings.register(this);
    }

    public void register(final PipeApplicationView view) {
        manager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE)) {
                    PetriNet petriNet = (PetriNet) evt.getNewValue();
                    view.registerNewPetriNet(petriNet);
                } else if (evt.getPropertyName().equals(PetriNetManagerImpl.REMOVE_PETRI_NET_MESSAGE)) {
                    view.removeCurrentTab();
                }

            }
        });
    }

    /**
     * Creates an empty Petri net with a default token
     */
    public void createEmptyPetriNet() {
        manager.createNewPetriNet();
    }

    //TODO: THIS IS RATHER UGLY, too many params but better than what was here before
    public void registerTab(PetriNet net, PetriNetTab tab, Observer historyObserver, UndoableEditListener undoListener,
                            PropertyChangeListener zoomListener) {
        AnimationHistory animationHistory = new AnimationHistory();
        animationHistory.addObserver(historyObserver);
        GUIAnimator animator = new GUIAnimator(new PetriNetAnimator(net), animationHistory);

        CopyPasteManager copyPasteManager = new CopyPasteManager(undoListener, tab, net);

        ZoomController zoomController = new ZoomController(100);
        tab.addZoomListener(zoomController);
        PetriNetController petriNetController =
                new PetriNetController(net, undoListener, animator, copyPasteManager, zoomController, tab);
        SelectionManager selectionManager = new SelectionManager(tab, petriNetController);
        selectionManagers.put(tab, selectionManager);
        netControllers.put(tab, petriNetController);

        PetriNetMouseHandler handler =
                new PetriNetMouseHandler(applicationModel, new SwingMouseUtilities(), petriNetController, tab);
        tab.addMouseListener(handler);
        tab.addMouseMotionListener(handler);
        tab.addMouseWheelListener(handler);

        tab.updatePreferredSize();

        PropertyChangeListener changeListener =
                new PetriNetChangeListener(tab.getApplicationView(), tab, petriNetController);
        net.addPropertyChangeListener(changeListener);

        setActiveTab(tab);
        initialiseNet(net, changeListener);
    }

    public void setActiveTab(PetriNetTab tab) {
        this.activeTab = tab;
    }

    /**
     * This is a little hacky, I'm not sure how to make this better when it's so late
     * If a better implementation is clear please re-write
     * <p/>
     * This method invokes the change listener which will create the view objects on the
     * petri net tab
     *
     * @param propertyChangeListener
     */
    private void initialiseNet(PetriNet net, PropertyChangeListener propertyChangeListener) {
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

    public void createNewTabFromFile(File file) throws UnparsableException {
        try {
            manager.createFromFile(file);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new UnparsableException("Could not initialise Petri net reader!");
        }
    }

    //TODO: DELETE
    public boolean isPasteEnabled() {
        return false;
        //        return copyPasteManager.pasteEnabled();
    }

    public void saveAsCurrentPetriNet(File outFile)
            throws ParserConfigurationException, TransformerException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        PetriNetController petriNetController = getActivePetriNetController();
        PetriNet petriNet = petriNetController.getPetriNet();

        try {
            manager.savePetriNet(petriNet, outFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write!");
        }
        petriNetController.save();
    }

    public PetriNetController getActivePetriNetController() {
        return netControllers.get(activeTab);
    }

    public SelectionManager getSelectionManager(PetriNetTab petriNetTab) {
        return selectionManagers.get(petriNetTab);
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
     * Removes the active tab from display.
     * Note active tab must be removed from netControllers before the petri net is removed
     * from the manager because the manager will fire a message which causes the active tab
     * to be swapped to the new open tab
     */
    public void removeActiveTab() {
        PetriNetController controller = netControllers.get(activeTab);
        netControllers.remove(activeTab);
        PetriNet petriNet = controller.getPetriNet();
        manager.remove(petriNet);
    }
}
