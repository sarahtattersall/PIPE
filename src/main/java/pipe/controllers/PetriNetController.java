package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.handlers.ArcKeyboardEventHandler;
import pipe.models.*;
import pipe.views.PetriNetView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class PetriNetController implements IController, Serializable {

    private static class PetriNetInformation {

        public final PetriNetView view;
        public int placeCount = 0;
        public int transitionCount = 0;

        PetriNetInformation(PetriNetView view) {
            this.view = view;
        }
    }

    private HashMap<PetriNet, PetriNetInformation> petriNetInfo = new
            HashMap<PetriNet, PetriNetInformation>();
//    private ArrayList<PetriNetView> _views = new ArrayList<PetriNetView>();
//    private ArrayList<PetriNet> _models = new ArrayList<PetriNet>();
//    private List<Integer> _placeNumbers = new LinkedList<Integer>();
//    private List<Integer> _transitionNumbers = new LinkedList<Integer>();
    private PetriNet _activePetriNet = null;
    private boolean currentlyCreatingArc = false;
    private NormalArc arc;

    private final Set<PetriNetComponent> selectedComponents = new
            HashSet<PetriNetComponent>();


    //THis needs to be moved into its own logical class
    private Connectable source;


    public PetriNetView getView() {
        return petriNetInfo.get(_activePetriNet).view;
    }

    public PetriNetView addPetriNet(PetriNet model) {
        PetriNetView petriNetView = new PetriNetView(this, model);
        PetriNetInformation info = new PetriNetInformation(petriNetView);
        petriNetInfo.put(model, info);
        _activePetriNet = model;
        return petriNetView;
    }

    public PetriNetView addEmptyPetriNet() {
        PetriNet petriNet = new PetriNet();
        return addPetriNet(petriNet);
    }

    public void addArcPoint(double x, double y, boolean shiftDown) {
        if (currentlyCreatingArc) {
            arc.setTarget(new TemporaryArcTarget(x, y));

            _activePetriNet.notifyObservers();
            //arc.setTarget(null);
            //_createArcView.setEndPoint(Grid.getModifiedX(event.getX()),
            // Grid.getModifiedY(event.getY()), event.isShiftDown());
        }
    }

    /**
     * Starts creating an arc from the source.
     *
     * @param source source model
     */
    //TODO: handle different arc types.
    public void startCreatingArc(Connectable source) {
        currentlyCreatingArc = true;
        this.arc = buildEmptyArc(source);
        addArcToCurrentPetriNet(arc);
        this.source = source;
    }

    private void addArcToCurrentPetriNet(NormalArc arc) {
        _activePetriNet.addArc(arc);
    }

    private NormalArc buildEmptyArc(Connectable source) {
        return new NormalArc(source,
                             new TemporaryArcTarget(source.getX(),
                                                    source.getY()),
                             new HashMap<Token, String>());
    }

    public boolean isCurrentlyCreatingArc() {
        return currentlyCreatingArc;
    }

    public void cancelArcCreation() {
        currentlyCreatingArc = false;
        _activePetriNet.remove(arc);
    }

    public void finishCreatingArc(Connectable target) {
        arc.setTarget(target);
        currentlyCreatingArc = false;
    }

    /**
     * Returns true if creatingArc and if the potentialEnd is not of
     * the same class as the source.
     *
     * @param potentialEnd
     * @return true if arc can end on the connectable
     */
    public boolean isApplicableEndPoint(Connectable potentialEnd) {
        if (currentlyCreatingArc && potentialEnd.isEndPoint()) {
            return potentialEnd.getClass() != arc.getSource().getClass();
        }
        return false;
    }

    /**
     * Creates unique petri net numbers for each tab
     *
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniquePlaceNumber() {
        PetriNetInformation info = petriNetInfo.get(_activePetriNet);
        int returnValue = info.placeCount;
        info.placeCount++;
        return returnValue;
    }

    /**
     * Creates unique petri net numbers for each tab
     *
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniqueTransitionNumber() {
        PetriNetInformation info = petriNetInfo.get(_activePetriNet);
        int returnValue = info.transitionCount;
        info.transitionCount++;
        return returnValue;
    }

    public void select(PetriNetComponent component) {
        selectedComponents.add(component);
    }

    public boolean isSelected(PetriNetComponent component) {
        return selectedComponents.contains(component);
    }

    public void deselect(PetriNetComponent component) {
        selectedComponents.remove(component);
    }

    public void deselectAll() {
        selectedComponents.clear();
    }

    public void translateSelected(Point2D.Double translation) {
        for (PetriNetComponent component : selectedComponents) {
            if (component instanceof Connectable) {
                Connectable connectable = (Connectable) component;
                connectable.setX(connectable.getX() + translation.getX());
                connectable.setY(connectable.getY() + translation.getY());
            }

        }
        _activePetriNet.notifyObservers();
    }


    /**
     * Selects all components within this rectangle
     *
     * @param selectionRectangle
     */
    public void select(Rectangle selectionRectangle) {
        for (Place place : _activePetriNet.getPlaces()) {
            selectConnectable(place, selectionRectangle);
        }
        for (Transition transition : _activePetriNet.getTransitions()) {
            selectConnectable(transition, selectionRectangle);
        }
        for (Arc arc : _activePetriNet.getArcs()) {
            if (selectedComponents.contains(arc.getSource()) ||
                    selectedComponents.contains(arc.getTarget())) {
                select(arc);
            }
        }

    }

    /**
     * Currently must be of type Connectable, since yhis is the only abstract
     * class containing getters for X and Y
     *
     * @param connectable        object to select
     * @param selectionRectangle
     */
    private void selectConnectable(Connectable connectable,
                                   Rectangle selectionRectangle) {
        int x = new Double(connectable.getX()).intValue();
        int y = new Double(connectable.getY()).intValue();
        Rectangle rectangle = new Rectangle(x, y, connectable.getHeight(),
                                            connectable.getWidth());
        if (selectionRectangle.intersects(rectangle)) {
            select(connectable);
        }
    }

    public void deleteSelection() {
        for (PetriNetComponent component : selectedComponents) {
            _activePetriNet.remove(component);
        }
        selectedComponents.clear();
        _activePetriNet.notifyObservers();
    }

    /**
     *
     * @param component
     * @param tokenName
     */
    public void addTokenToPlace(Place component, String tokenName) {
        //TODO: Find an O(1) way to do this.... maybe map id to name?
        for (Token token : _activePetriNet.getTokens()) {
            if (token.getId().equals(tokenName)) {
                component.incrementTokenCount(token);
                _activePetriNet.notifyObservers();
                return;
            }
        }

        throw new RuntimeException("No " + tokenName + " token found in current petri net");
    }
}
