package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.handlers.ArcKeyboardEventHandler;
import pipe.models.*;
import pipe.views.ArcView;
import pipe.views.PetriNetView;
import pipe.views.builder.NormalArcViewBuilder;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PetriNetController implements IController, Serializable
{

    private ArrayList<PetriNetView> _views = new ArrayList<PetriNetView>();
    private ArrayList<PetriNet> _models = new ArrayList<PetriNet>();
    private List<Integer> _petriNetNumbers = new LinkedList<Integer>();
    private int _activePetriNet = 0;
    private boolean currentlyCreatingArc = false;
    private NormalArc arc;
    private ArcView arcView;

    //THis needs to be moved into its own logical class
    private Connectable source;

    public PetriNetController()
    {
        PlaceController placeController = new PlaceController();
        TransitionController transitionController = new TransitionController();
        TokenController tokenController = new TokenController();
        MarkingController markingController = new MarkingController(tokenController);
        _petriNetNumbers.add(0);
    }

    public PetriNetView getView()
    {
        return _views.get(_activePetriNet);
    }

    public void addPetriNet(PetriNet model)
    {
        _models.add(model);
        PetriNetView petriNetView = new PetriNetView(this, model);
        _views.add(petriNetView);
        _petriNetNumbers.add(0);
        changeActivePetriNet();
    }

    public PetriNetView addEmptyPetriNet()
    {
        PetriNet petriNet = new PetriNet();
        PetriNetView petriNetView = new PetriNetView(this, petriNet);
        _views.add(petriNetView);
        _models.add(petriNet);
        _petriNetNumbers.add(0);
        changeActivePetriNet();
        return petriNetView;
    }

    private void changeActivePetriNet()
    {
        _activePetriNet = _models.size() - 1;
    }

    public void addArcPoint(double x, double y, boolean shiftDown) {
        if (currentlyCreatingArc) {
            arc.setTargetLocation(new Point2D.Double(x, y));
            //arc.setTarget(null);
            //_createArcView.setEndPoint(Grid.getModifiedX(event.getX()), Grid.getModifiedY(event.getY()), event.isShiftDown());
        }
    }

    /**
     * Starts creating an arc from the source.
     * @param source source model
     */
    //TODO: handle different arc types.
    public void startCreatingArc(Connectable source) {
        currentlyCreatingArc = true;
        this.arc = new NormalArc(source, null, new LinkedList<Marking>());
        NormalArcViewBuilder builder = new NormalArcViewBuilder(arc);
        arcView = builder.build();
        PetriNetTab tab = ApplicationSettings.getApplicationView().getCurrentTab();
        tab.add(arcView);
        arc.registerObserver(arcView);
        arcView.setSourceLocation(source.getCentreX(), source.getCentreY());
        ArcKeyboardEventHandler keyHandler = new ArcKeyboardEventHandler(arcView);
        arcView.addKeyListener(keyHandler);
        this.source = source;
    }

    public boolean isCurrentlyCreatingArc() {
        return currentlyCreatingArc;
    }

    public void cancelArcCreation() {
        currentlyCreatingArc = false;
        arcView.removeFromView();
        arcView.delete();
    }

    public void finishCreatingArc(Connectable target) {
        arc.setTarget(target);
        currentlyCreatingArc = false;
    }

    /**
     *
     * Returns true if creatingArc and if the potentialEnd is not of
     * the same class as the source.
     *
     * @param potentialEnd
     * @return true if arc can end on the connectable
     *
     */
    public boolean isApplicableEndPoint(Connectable potentialEnd) {
        return currentlyCreatingArc && potentialEnd.getClass() != arc.getSource().getClass();
    }

    /**
     * Creates unique petri net numbers for each tab
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniquePlaceNumber() {
        int returnValue = _petriNetNumbers.get(_activePetriNet);
        _petriNetNumbers.set(_activePetriNet, returnValue + 1);
        return returnValue;
    }
}
