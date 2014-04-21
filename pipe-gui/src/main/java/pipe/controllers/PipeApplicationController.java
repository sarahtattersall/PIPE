package pipe.controllers;

import pipe.animation.PetriNetAnimator;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.SwingMouseUtilities;
import pipe.historyActions.AnimationHistory;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.manager.PetriNetManager;
import pipe.models.manager.PetriNetManagerImpl;
import pipe.models.petrinet.PetriNet;
import pipe.models.petrinet.name.PetriNetFileName;
import pipe.models.petrinet.name.PetriNetName;
import pipe.naming.PetriNetNamer;
import pipe.parsers.UnparsableException;
import pipe.views.PipeApplicationView;
import pipe.views.changeListener.PetriNetChangeListener;

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

    /**
     * Responsible for naming petri nets
     */
    private final PetriNetNamer petriNetNamer = new PetriNetNamer();

    private final PipeApplicationModel applicationModel;

    /**
     * The current tab displayed in the view
     */
    private PetriNetTab activeTab;

    /**
     * Manages creation/deletion of Petri net models
     */
    private final PetriNetManager manager = new PetriNetManagerImpl();

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
               } else if (evt.getPropertyName().equals(PetriNetManagerImpl.REMOVE_PETRI_NET_MESSAGE))  {
                   view.removeCurrentTab();
               }

           }
       });
    }

    /**
     * Creates an empty petrinet with a default token
     */
    public void createEmptyPetriNet() {
        manager.createNewPetriNet();
    }

    /**
     * Names the petri net based on the file
     *
     * @param petriNet petri net loaded from file
     * @param file     xml location
     */
    private void namePetriNetFromFile(PetriNet petriNet, File file) {
        PetriNetName petriNetName = new PetriNetFileName(file);
        petriNet.setName(petriNetName);
        petriNetNamer.registerPetriNet(petriNet);
    }


    //TODO: THIS IS RATHER UGLY, too many params but better than what was here before
    public void registerTab(PetriNet net, PetriNetTab tab, Observer historyObserver, UndoableEditListener undoListener, PropertyChangeListener zoomListener) {
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

    public void setActiveTab(PetriNetTab tab) {
        this.activeTab = tab;
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
     *
     * @return true if the current petri net has changed
     */
    public boolean hasCurrentPetriNetChanged() {
        PetriNetController activeController = getActivePetriNetController();
        return activeController != null && activeController.hasChanged();
    }

    public boolean anyNetsChanged() {
        return !getNetsChanged().isEmpty();
    }

    public void removeActiveTab() {
        manager.remove(netControllers.get(activeTab).getPetriNet());
        netControllers.remove(activeTab);
    }
}
