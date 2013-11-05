package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.models.*;
import pipe.views.PetriNetView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class PetriNetController implements IController, Serializable
{

    private ArrayList<PetriNetView> _views = new ArrayList<PetriNetView>();
    private ArrayList<PetriNet> _models = new ArrayList<PetriNet>();
    private List<Integer> _placeNumbers = new LinkedList<Integer>();
    private List<Integer> _transitionNumbers = new LinkedList<Integer>();
    private int _activePetriNet = 0;
    private boolean currentlyCreatingArc = false;
    private NormalArc arc;

    private final Set<PetriNetComponent> selectedComponents = new HashSet<PetriNetComponent>();


    //THis needs to be moved into its own logical class
    private Connectable source;

    public PetriNetController()
    {
        PlaceController placeController = new PlaceController();
        TransitionController transitionController = new TransitionController();
        TokenController tokenController = new TokenController();
        MarkingController markingController = new MarkingController(tokenController);
        _placeNumbers.add(0);
        _transitionNumbers.add(0);
    }

    public PetriNetView getView()
    {
        return _views.get(_activePetriNet);
    }

    public PetriNetView addPetriNet(PetriNet model)
    {
        _models.add(model);
        PetriNetView petriNetView = new PetriNetView(this, model);
        _views.add(petriNetView);
        _placeNumbers.add(0);
        _transitionNumbers.add(0);
        changeActivePetriNet();
        return petriNetView;
    }

    public PetriNetView addEmptyPetriNet()
    {
        PetriNet petriNet = new PetriNet();
        PetriNetView petriNetView = new PetriNetView(this, petriNet);
        _views.add(petriNetView);
        _models.add(petriNet);
        _placeNumbers.add(0);
        _transitionNumbers.add(0);
        changeActivePetriNet();
        return petriNetView;
    }

    private void changeActivePetriNet()
    {
        _activePetriNet = _models.size() - 1;
    }

    public void addArcPoint(double x, double y, boolean shiftDown) {
        if (currentlyCreatingArc) {
            arc.setTarget(new TemporaryArcTarget(x, y));

            PetriNet activeModel = _models.get(_activePetriNet);
            activeModel.notifyObservers();
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
        this.arc = buildEmptyArc(source);;
        addArcToCurrentPetriNet(arc);
        this.source = source;
    }

    private void addArcToCurrentPetriNet(NormalArc arc) {
        PetriNet activeModel = _models.get(_activePetriNet);
        activeModel.addArc(arc);
    }

    private NormalArc buildEmptyArc(Connectable source) {
        return new NormalArc(source,
                new TemporaryArcTarget(source.getX(), source.getY()),
                new LinkedList<Marking>());
    }

    public boolean isCurrentlyCreatingArc() {
        return currentlyCreatingArc;
    }

    public void cancelArcCreation() {
        currentlyCreatingArc = false;
        //TODO: Delete arc from petrinet!
        //arcView.removeFromView();
        //arcView.delete();
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
        if (currentlyCreatingArc && potentialEnd.isEndPoint())
        {
            return potentialEnd.getClass() != arc.getSource().getClass();
        }
        return false;
    }

    /**
     * Creates unique petri net numbers for each tab
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniquePlaceNumber() {
        int returnValue = _placeNumbers.get(_activePetriNet);
        _placeNumbers.set(_activePetriNet, returnValue + 1);
        return returnValue;
    }

    /**
     * Creates unique petri net numbers for each tab
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniqueTransitionNumber() {
        int returnValue = _transitionNumbers.get(_activePetriNet);
        _transitionNumbers.set(_activePetriNet, returnValue + 1);
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

    public void translateSelected(Point2D.Double translation)
    {
        PetriNet activeModel = _models.get(_activePetriNet);
        for (PetriNetComponent component : selectedComponents) {
            if (component instanceof Connectable) {
                Connectable connectable = (Connectable) component;
                connectable.setX(connectable.getX() + translation.getX());
                connectable.setY(connectable.getY() + translation.getY());
            }

        }
        activeModel.notifyObservers();
    }


    /**
     * Selects all components within this rectangle
     * @param selectionRectangle
     */
    public void select(Rectangle selectionRectangle) {
        PetriNet activeModel = _models.get(_activePetriNet);
        for (Place place : activeModel.getPlaces()) {
            selectConnectable(place, selectionRectangle);
        }
        for (Transition transition : activeModel.getTransitions()) {
            selectConnectable(transition, selectionRectangle);
        }
        for(Arc arc : activeModel.getArcs())
        {
            if (selectedComponents.contains(arc.getSource()) ||
                selectedComponents.contains(arc.getTarget()))
            {
                select(arc);
            }
        }

    }

    /**
     *
     * Currently must be of type Connectable, since yhis is the only abstract class containing getters for X and Y
     *
     * @param connectable object to select
     * @param selectionRectangle
     */
    private void selectConnectable(Connectable connectable, Rectangle selectionRectangle) {
        int x = new Double(connectable.getX()).intValue();
        int y = new Double(connectable.getY()).intValue();
        Rectangle rectangle = new Rectangle(x, y, connectable.getHeight(), connectable.getWidth());
        if (selectionRectangle.intersects(rectangle)) {
            select(connectable);
        }
    }

    public void deleteSelection() {
        PetriNet activeModel = _models.get(_activePetriNet);
        for (PetriNetComponent component : selectedComponents)
        {
            activeModel.remove(component);
        }
        selectedComponents.clear();
        activeModel.notifyObservers();
    }
}
