package pipe.controllers;

import org.apache.commons.io.FilenameUtils;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.SwingMouseUtilities;
import pipe.historyActions.AnimationHistory;
import pipe.historyActions.HistoryManager;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.*;
import pipe.io.PetriNetIOImpl;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.views.PipeApplicationView;
import pipe.views.changeListener.PetriNetChangeListener;
import pipe.views.changeListener.TokenChangeListener;

import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PipeApplicationController {

    private final Map<PetriNetTab, PetriNetController> netControllers = new HashMap<PetriNetTab, PetriNetController>();

    private final Map<PetriNetTab, SelectionManager> selectionManagers = new HashMap<PetriNetTab, SelectionManager>();

    //TODO: Circular dependency between these two classes
    private final PipeApplicationModel applicationModel;

    private PetriNetTab activeTab;

    public PipeApplicationController(PipeApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        ApplicationSettings.register(this);
    }

    /**
     * Creates an empty petrinet with a default token
     */
    public PetriNetTab createEmptyPetriNet(PipeApplicationView applicationView) {
        PetriNet model = new PetriNet();
        Token defaultToken = createDefaultToken(applicationView);
        model.addToken(defaultToken);
        return createNewTab(model, applicationView);
    }

    private Token createDefaultToken(PipeApplicationView applicationView) {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        token.addPropertyChangeListener(new TokenChangeListener(applicationView));
        return token;
    }

    public PetriNetTab createNewTab(PetriNet net, PipeApplicationView applicationView) {
        AnimationHistory animationHistory = new AnimationHistory();
        Animator animator = new Animator(net, animationHistory);

        ZoomController zoomController = new ZoomController(100);

        AnimationHistoryView animationHistoryView;
        try {
            animationHistoryView = new AnimationHistoryView(animationHistory, "Animation History");
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException();
        }
        animationHistory.addObserver(animationHistoryView);


        PetriNetTab petriNetTab = new PetriNetTab(zoomController, animationHistoryView);

        CopyPasteManager copyPasteManager = new CopyPasteManager(petriNetTab, net);

        PetriNetController petriNetController =
                new PetriNetController(net, new HistoryManager(applicationView), animator, copyPasteManager,
                        zoomController);


        SelectionManager selectionManager = new SelectionManager(petriNetTab, petriNetController);
        selectionManagers.put(petriNetTab, selectionManager);


        netControllers.put(petriNetTab, petriNetController);

        PetriNetMouseHandler handler =
                new PetriNetMouseHandler(new SwingMouseUtilities(), petriNetController, net, petriNetTab);
        petriNetTab.addMouseListener(handler);
        petriNetTab.addMouseMotionListener(handler);
        petriNetTab.addMouseWheelListener(handler);

        String name;
        if (net.getPnmlName().isEmpty()) {
            name = "Petri net " + (applicationModel.newPetriNetNumber());
        } else {
            name = FilenameUtils.getBaseName(net.getPnmlName());
        }

        petriNetTab.setNetChanged(false); // Status is unchanged


        petriNetTab.updatePreferredSize();

        PetriNetChangeListener changeListener = new PetriNetChangeListener(applicationView, petriNetTab, petriNetController);
        net.addPropertyChangeListener(changeListener);

        setActiveTab(petriNetTab);
        initialiseNet(net, changeListener);
        applicationView.addNewTab(name, petriNetTab);

        return petriNetTab;
    }

    /**
     * This is a little hacky, I'm not sure how to make this better when it's so late
     * If a better implementation is clear please re-write
     *
     * This method invokes the change listener which will create the view objects on the
     * petri net tab
     * @param propertyChangeListener
     */
    private void initialiseNet(PetriNet net, PropertyChangeListener propertyChangeListener) {
        for (Token token : net.getTokens()) {
            PropertyChangeEvent changeEvent = new PropertyChangeEvent(net, "newToken", null, token);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Place place : net.getPlaces()) {
            PropertyChangeEvent changeEvent = new PropertyChangeEvent(net, "newPlace", null, place);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Transition transition : net.getTransitions()) {
            PropertyChangeEvent changeEvent = new PropertyChangeEvent(net, "newTransition", null, transition);
            propertyChangeListener.propertyChange(changeEvent);
        }

        for (Arc<? extends Connectable, ? extends Connectable> arc : net.getArcs()) {
            PropertyChangeEvent changeEvent = new PropertyChangeEvent(net, "newArc", null, arc);
            propertyChangeListener.propertyChange(changeEvent);
        }
    }

    public PetriNetTab createNewTabFromFile(File file, PipeApplicationView applicationView, boolean isTN) {

        if (isPasteInProgress()) {
            cancelPaste();
        }


        try {
            pipe.io.PetriNetReader petriNetIO = new PetriNetIOImpl();
            PetriNet net = petriNetIO.read(file.getAbsolutePath());
            return createNewTab(net, applicationView);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: DELETE
    public boolean isPasteInProgress() {
        return false;
        //        return copyPasteManager.pasteInProgress();
    }

    //TODO: DELETE
    public void cancelPaste() {

        //        copyPasteManager.cancelPaste();
    }

    //TODO: DELETE
    public CopyPasteManager getCopyPasteManager() {
        return null;
    }

    //TODO: DELETE
    public boolean isPasteEnabled() {
        return false;
        //        return copyPasteManager.pasteEnabled();
    }

    //TODO: DELETE
    public void copy(ArrayList selection, PetriNetTab appView) {
        //        copyPasteManager.doCopy(selection, appView);
    }

    public PetriNetController getControllerForTab(PetriNetTab tab) {
        return netControllers.get(tab);
    }

    public void setActiveTab(PetriNetTab tab) {
        this.activeTab = tab;
    }

    public void saveCurrentPetriNet(File outFile, boolean saveFunctional)
            throws ParserConfigurationException, TransformerException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        PetriNetController petriNetController = getActivePetriNetController();
        PetriNet petriNet = petriNetController.getPetriNet();


        //TODO: WORK OUT WHAT TO DO WITH SAVE FUNCTIONAL
        try {
            pipe.io.PetriNetWriter writer = new PetriNetIOImpl();
            writer.writeTo(outFile.getAbsolutePath(), petriNet);
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
}
