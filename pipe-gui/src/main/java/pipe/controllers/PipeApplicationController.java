package pipe.controllers;

import pipe.actions.manager.SimpleUndoListener;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.SwingMouseUtilities;
import pipe.historyActions.AnimationHistory;
import pipe.historyActions.HistoryManager;
import pipe.io.PetriNetIOImpl;
import pipe.io.PetriNetReader;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.models.petrinet.name.NormalPetriNetName;
import pipe.models.petrinet.name.PetriNetFileName;
import pipe.models.petrinet.name.PetriNetName;
import pipe.naming.PetriNetNamer;
import pipe.parsers.UnparsableException;
import pipe.views.PipeApplicationView;
import pipe.views.changeListener.PetriNetChangeListener;
import pipe.views.changeListener.TokenChangeListener;

import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeApplicationController {

    private final Map<PetriNetTab, PetriNetController> netControllers = new HashMap<>();

    private final Map<PetriNetTab, SelectionManager> selectionManagers = new HashMap<>();

    private final PetriNetNamer petriNetNamer = new PetriNetNamer();

    private PetriNetTab activeTab;

    public PipeApplicationController(PipeApplicationModel applicationModel) {
        ApplicationSettings.register(this);
    }

    /**
     * Creates an empty petrinet with a default token
     */
    public PetriNetTab createEmptyPetriNet(PipeApplicationView applicationView) {
        PetriNet model = createEmptyNet(applicationView);
        namePetriNet(model);
        return createNewTab(model, applicationView);
    }

    /**
     * Creates an empty petri net and default token
     * Registers the application view to listen on the default token
     *
     * @param applicationView
     * @return new petri net
     */
    private PetriNet createEmptyNet(PipeApplicationView applicationView) {
        PetriNet model = new PetriNet();
        Token defaultToken = createDefaultToken(applicationView);
        model.addToken(defaultToken);
        return model;
    }

    /**
     * Names the petri net with a unique name
     * Adds petri net to the unique namer so not to produce the same name twice
     *
     * @param petriNet petri net to name
     */
    private void namePetriNet(PetriNet petriNet) {
        String name = petriNetNamer.getName();
        PetriNetName petriNetName = new NormalPetriNetName(name);
        petriNet.setName(petriNetName);
        petriNetNamer.registerPetriNet(petriNet);
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

    private Token createDefaultToken(PipeApplicationView applicationView) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        token.addPropertyChangeListener(new TokenChangeListener(applicationView));
        return token;
    }

    public PetriNetTab createNewTab(PetriNet net, final PipeApplicationView applicationView) {
        AnimationHistory animationHistory = new AnimationHistory();
        Animator animator = new Animator(net, animationHistory);

        ZoomController zoomController = new ZoomController(100);
        zoomController.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                applicationView.updateZoomCombo();
            }
        });

        AnimationHistoryView animationHistoryView;
        try {
            animationHistoryView = new AnimationHistoryView(animationHistory, "Animation History");
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException();
        }
        animationHistory.addObserver(animationHistoryView);


        PetriNetTab petriNetTab = new PetriNetTab(zoomController, animationHistoryView);

        CopyPasteManager copyPasteManager =
                new CopyPasteManager(new SimpleUndoListener(applicationView.getComponentEditorManager(), this),
                        petriNetTab, net);

        PetriNetController petriNetController =
                new PetriNetController(net, new HistoryManager(applicationView.getComponentEditorManager()), animator,
                        copyPasteManager, zoomController, petriNetTab);


        SelectionManager selectionManager = new SelectionManager(petriNetTab, petriNetController);
        selectionManagers.put(petriNetTab, selectionManager);


        netControllers.put(petriNetTab, petriNetController);

        PetriNetMouseHandler handler =
                new PetriNetMouseHandler(new SwingMouseUtilities(), petriNetController, net, petriNetTab);
        petriNetTab.addMouseListener(handler);
        petriNetTab.addMouseMotionListener(handler);
        petriNetTab.addMouseWheelListener(handler);

        petriNetTab.updatePreferredSize();

        PropertyChangeListener changeListener =
                new PetriNetChangeListener(applicationView, petriNetTab, petriNetController);
        net.addPropertyChangeListener(changeListener);

        setActiveTab(petriNetTab);
        initialiseNet(net, changeListener);
        applicationView.addNewTab(net.getNameValue(), petriNetTab);

        return petriNetTab;
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

    public PetriNetTab createNewTabFromFile(File file, PipeApplicationView applicationView) throws UnparsableException {
        try {
            PetriNetReader petriNetIO = new PetriNetIOImpl();
            PetriNet net = petriNetIO.read(file.getAbsolutePath());
            namePetriNetFromFile(net, file);
            return createNewTab(net, applicationView);
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
            pipe.io.PetriNetWriter writer = new PetriNetIOImpl();
            writer.writeTo(outFile.getAbsolutePath(), petriNet);
            petriNetNamer.deRegisterPetriNet(petriNet);
            namePetriNetFromFile(petriNet, outFile);
            petriNetController.save();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write!");
        }
    }

    public PetriNetController getActivePetriNetController() {
        return netControllers.get(activeTab);  //To change body of created methods use File | Settings | File Templates.
    }

    public SelectionManager getSelectionManager(PetriNetTab petriNetTab) {
        return selectionManagers.get(petriNetTab);
    }

    /**
     * @return the names of the petri nets that have changed
     */
    //FIXME: THIS DOESNT WORK BECAUSE IT CHOOSES NEW NAMES FOR THE PETRI NET
    public List<String> getNetsChanged() {
        List<String> changed = new ArrayList<>();
        for (PetriNetController controller : netControllers.values()) {
            if (controller.hasChanged()) {
                changed.add(controller.getPetriNet().getNameValue());
            }
        }
        return changed;
    }

    public boolean anyNetsChanged() {
        return !getNetsChanged().isEmpty();
    }
}
