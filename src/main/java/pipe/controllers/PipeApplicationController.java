package pipe.controllers;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import pipe.gui.*;
import pipe.handlers.PetriNetMouseHandler;
import pipe.handlers.mouse.SwingMouseUtilities;
import pipe.historyActions.AnimationHistory;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.models.component.Transition;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.models.strategy.arc.ForwardsNormalStrategy;
import pipe.models.strategy.arc.InhibitorStrategy;
import pipe.petrinet.reader.PetriNetReader;
import pipe.petrinet.reader.creator.*;
import pipe.petrinet.writer.PetriNetWriter;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.utilities.transformers.TNTransformer;
import pipe.views.changeListener.PetriNetChangeListener;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PipeApplicationController {

    private final Map<PetriNetTab, PetriNetController> netControllers = new HashMap<PetriNetTab, PetriNetController>();
    private final CopyPasteManager copyPasteManager;
    //TODO: Circular dependency between these two classes
    private final PipeApplicationModel applicationModel;
    private PetriNetTab activeTab;

    public PipeApplicationController(CopyPasteManager copyPasteManager,
                                     PipeApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        this.copyPasteManager = copyPasteManager;
        ApplicationSettings.register(this);
    }

    /**
     * Creates an empty petrinet with a default token
     */
    public PetriNetTab createEmptyPetriNet() {
        PetriNet model = new PetriNet();
        Token defaultToken = createDefaultToken();
        model.addToken(defaultToken);
        return createNewTab(model);
    }

    private PetriNetTab createNewTab(PetriNet net) {
        AnimationHistory animationHistory = new AnimationHistory();
        Animator animator = new Animator(net, animationHistory);

        PetriNetController petriNetController =
                new PetriNetController(net, new HistoryManager(ApplicationSettings.getApplicationController()),
                        animator);
        AnimationHistoryView animationHistoryView;
        try {
            animationHistoryView = new AnimationHistoryView(animationHistory, "Animation History");
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException();
        }

        animationHistory.addObserver(animationHistoryView);


        PetriNetTab petriNetTab = new PetriNetTab(petriNetController, animationHistoryView);
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

        ApplicationSettings.getApplicationView().addNewTab(name, petriNetTab);

        petriNetTab.updatePreferredSize();

        //        net.notifyObservers();
        net.addPropertyChangeListener(new PetriNetChangeListener(petriNetTab, petriNetController));

        return petriNetTab;
    }

    private Token createDefaultToken() {
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        return token;
    }

    public PetriNetTab createNewTabFromFile(File file, boolean isTN) {

        if (isPasteInProgress()) {
            cancelPaste();
        }


        PetriNet net = new PetriNet();
        PetriNetTab tab = createNewTab(net);
        loadPetriNetFromFile(file, net, isTN);
        return tab;

    }

    public void cancelPaste() {
        copyPasteManager.cancelPaste();
    }

    public boolean isPasteInProgress() {
        return copyPasteManager.pasteInProgress();
    }

    private PetriNet loadPetriNetFromFile(File file, PetriNet net, boolean isTN) {

        try {
            // BK 10/02/07: Changed loading of PNML to accomodate new
            // PNMLTransformer class
            Document document;
            if (isTN) {
                TNTransformer transformer = new TNTransformer();
                document = transformer.transformTN(file.getPath());
            } else {
                // ProgressBar pb = new ProgressBar("test");
                PNMLTransformer transformer = new PNMLTransformer();
                document = transformer.transformPNML(file.getPath());
                //petriNetTab.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
            }
            ArcStrategy<Place, Transition> inhibitorStrategy = new InhibitorStrategy();
            ArcStrategy<Transition, Place> normalForwardStrategy = new ForwardsNormalStrategy(net);
            ArcStrategy<Place, Transition> normalBackwardStrategy = new BackwardsNormalStrategy(net);


            CreatorStruct struct = new CreatorStruct(new PlaceCreator(), new TransitionCreator(),
                    new ArcCreator(inhibitorStrategy, normalForwardStrategy, normalBackwardStrategy),
                    new AnnotationCreator(), new RateParameterCreator(), new TokenCreator(), new StateGroupCreator());
            PetriNetReader reader = new PetriNetReader(struct);
            reader.createFromFile(net, document);
            net.setPnmlName(file.getAbsolutePath());
            return net;

        } catch (Exception e) {
            //                JOptionPane.showMessageDialog(this, "Error loading file:\n" + file.getName() + "\n" + e.toString(),
            //                        "File load error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    public CopyPasteManager getCopyPasteManager() {
        return copyPasteManager;
    }

    public boolean isPasteEnabled() {
        return copyPasteManager.pasteEnabled();
    }

    public void copy(ArrayList selection, PetriNetTab appView) {
        copyPasteManager.doCopy(selection, appView);
    }

    public void showPasteRectangle(PetriNetTab appView) {
        copyPasteManager.showPasteRectangle(appView);
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
        PetriNetWriter writer = new PetriNetWriter();
        writer.writeToFile(petriNet, outFile.getAbsolutePath());
    }

    public PetriNetController getActivePetriNetController() {
        return netControllers.get(activeTab);  //To change body of created methods use File | Settings | File Templates.
    }

    public void setUndoActionEnabled(final boolean enabled) {
        ApplicationSettings.getApplicationView().setUndoActionEnabled(enabled);
    }

    public void setRedoActionEnabled(final boolean enabled) {
        ApplicationSettings.getApplicationView().setRedoActionEnabled(enabled);

    }
}
