package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.exceptions.InvalidRateException;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.Animator;
import pipe.gui.CopyPasteManager;
import pipe.gui.DragManager;
import pipe.gui.ZoomController;
import pipe.historyActions.*;
import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.PlaceablePetriNetComponent;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.naming.PetriNetComponentNamer;
import pipe.naming.PlaceNamer;
import pipe.naming.TransitionNamer;
import pipe.visitor.TranslationVisitor;
import pipe.visitor.foo.PetriNetComponentVisitor;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PetriNetController implements IController, Serializable {

    /**
     * Responsible for zooming of the current Petri net
     */
    private final ZoomController zoomController;

    /**
     * Deals with undo/redo history of current Petri net
     */
    private final HistoryManager historyManager;

    /**
     * Petri net being displayed
     */
    private final PetriNet petriNet;

    /**
     * Selected components in the Petri net
     */
    private final Set<PetriNetComponent> selectedComponents = new HashSet<>();

    /**
     * Responsible for copy and pasting of selected components
     */
    private final CopyPasteManager copyPasteManager;

    /**
     * Responsible for naming places
     */
    private final PetriNetComponentNamer placeNamer;

    /**
     * Responsible for creating unique transition names
     */
    private final PetriNetComponentNamer transitionNamer;

    /**
     * Token that is currently selected in the drop down
     */
    private Token selectedToken;

    /**
     * Animator class for animating tokens in the petri net
     */
    private Animator animator;


    /**
     * Drag manager for dragging selected objects
     */
    private DragManager dragManager = new DragManager(this);


    public PetriNetController(PetriNet model, HistoryManager historyManager, Animator animator,
                              CopyPasteManager copyPasteManager, ZoomController zoomController) {
        petriNet = model;
        this.zoomController = zoomController;
        this.animator = animator;
        this.copyPasteManager = copyPasteManager;
        if (model.getTokens().size() > 0) {
            selectedToken = model.getTokens().iterator().next();
        }
        this.historyManager = historyManager;
        placeNamer = new PlaceNamer(model);
        transitionNamer = new TransitionNamer(model);
    }

    /**
     * @return A unique name for a place in the current petri net
     */
    public String getUniquePlaceName() {
        return placeNamer.getName();
    }

    /**
     * @return A unique name for a transition in the current petri net
     */
    public String getUniqueTransitionName() {
        return transitionNamer.getName();
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
        PetriNetComponentVisitor translationVisitor = new TranslationVisitor(translation, selectedComponents);
        for (PetriNetComponent component : selectedComponents) {
            component.accept(translationVisitor);
        }
    }

    /**
     * Selects all components within this rectangle
     *
     * @param selectionRectangle bounds for selection
     */
    public void select(Rectangle selectionRectangle) {
        for (Place place : petriNet.getPlaces()) {
            selectPlaceable(place, selectionRectangle);
        }
        for (Transition transition : petriNet.getTransitions()) {
            selectPlaceable(transition, selectionRectangle);
        }
        for (Arc<? extends Connectable, ? extends Connectable> arc : petriNet.getArcs()) {
            if (isArcSelected(arc, selectionRectangle)) {
                select(arc);
                for (ArcPoint arcPoint : arc.getIntermediatePoints()) {
                    select(arcPoint);
                }
            } else if (selectedComponents.contains(arc.getSource()) || selectedComponents.contains(arc.getTarget())) {
                select(arc);
            }
        }
        for (Annotation annotation : petriNet.getAnnotations()) {
            selectPlaceable(annotation, selectionRectangle);
        }
    }

    /**
     * A crude method for selecting arcs, does not take into account bezier curves
     *
     * @param arc arc to test to see if it is selected
     * @param selectionRectangle bounds of selection on screen
     * @return if selectionRectangle intersects the path
     */
    private boolean isArcSelected(Arc<? extends Connectable, ? extends Connectable> arc, Rectangle selectionRectangle) {
        GeneralPath path = createStraightPath(arc);
        return path.intersects(selectionRectangle);
    }

    /**
     *
     * Creates an arc with a straight path arc
     *
     * @param arc
     * @return Straight path for arc, ignoring Bezier curves
     */
    private GeneralPath createStraightPath(Arc<? extends Connectable, ? extends Connectable> arc) {
        GeneralPath path = new GeneralPath();
        Point2D start = arc.getStartPoint();
        path.moveTo(start.getX(), start.getY());

        Collection<ArcPoint> arcPoints = arc.getIntermediatePoints();
        for (ArcPoint arcPoint : arcPoints) {
            Point2D point = arcPoint.getPoint();
            path.lineTo(point.getX(), point.getY());
        }

        Point2D end = arc.getEndPoint();
        path.lineTo(end.getX(), end.getY());
        return path;
    }

    public void select(PetriNetComponent component) {
        selectedComponents.add(component);
    }

    /**
     * Tests to see if the object is in the selection rectangle
     * If it is it selects in
     *
     * @param placeable          object to see if it is selectable
     * @param selectionRectangle bounds for selection
     */
    private void selectPlaceable(PlaceablePetriNetComponent placeable, Rectangle selectionRectangle) {
        int x = new Double(placeable.getX()).intValue();
        int y = new Double(placeable.getY()).intValue();
        Rectangle rectangle = new Rectangle(x, y, placeable.getHeight(), placeable.getWidth());
        if (selectionRectangle.intersects(rectangle)) {
            select(placeable);
        }
    }

    /**
     * Deletes selection and adds to history manager
     */
    public void deleteSelection() {
        historyManager.newEdit();

        for (PetriNetComponent component : selectedComponents) {
            deleteComponent(component);
        }
        selectedComponents.clear();
    }

    /**
     * Deletes a component adding it to the history managers current edit
     *
     * @param component
     */
    private void deleteComponent(PetriNetComponent component) {
        petriNet.remove(component);
        DeletePetriNetObject deleteAction = new DeletePetriNetObject(component, petriNet);
        historyManager.addEdit(deleteAction);
    }

    /**
     * Deletes single component, starts a newEdit for history manager
     *
     * @param component to delete
     */
    public void delete(PetriNetComponent component) {
        historyManager.newEdit();
        deleteComponent(component);
    }

    /**
     * Adds a new token to the petrinet
     *
     * @param name
     * @param enabled
     * @param color
     */
    public void createNewToken(String name, boolean enabled, Color color) {
        Token token = new Token(name, enabled, 0, color);
        petriNet.addToken(token);
    }

    public Collection<Token> getNetTokens() {
        return petriNet.getTokens();
    }

    public void updateToken(String currentTokenName, String name, boolean enabled, Color color)
            throws PetriNetComponentNotFoundException {
        Token token = petriNet.getToken(currentTokenName);
        if (!token.getId().equals(name)) {
            token.setId(name);
        }
        if (token.isEnabled() != enabled) {
            token.setEnabled(enabled);
        }
        if (!token.getColor().equals(color)) {
            token.setColor(color);
        }
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public <S extends Connectable, T extends Connectable> ArcController<S, T> getArcController(Arc<S, T> arc) {
        return new ArcController<S, T>(arc, historyManager);
    }

    public PlaceController getPlaceController(Place place) {
        return new PlaceController(place, historyManager);
    }

    public TransitionController getTransitionController(final Transition transition) {
        return new TransitionController(transition, historyManager);
    }

    public void selectToken(String tokenName) throws PetriNetComponentNotFoundException {
        selectToken(getToken(tokenName));
    }

    /**
     * @param name token name to find
     * @return Token from PetriNet
     * @throw RuntimeException if the token does not exist
     */
    public Token getToken(String name) throws PetriNetComponentNotFoundException {
        return petriNet.getToken(name);
    }

    //TODO: Should this be in the model???
    public void selectToken(Token token) {
        this.selectedToken = token;
    }

    public void copySelection() {
        copyPasteManager.copy(selectedComponents);
    }

    public boolean isCopyInProgress() {
        return copyPasteManager.pasteEnabled();
    }

    public void cancelPaste() {
        copyPasteManager.cancelPaste();
    }

    public Token getSelectedToken() {
        return selectedToken;
    }

    public Animator getAnimator() {
        return animator;
    }

    public ZoomController getZoomController() {
        return zoomController;
    }

    public void paste() {
        copyPasteManager.showPasteRectangle();
    }

    public boolean isPasteEnabled() {
        return copyPasteManager.pasteEnabled();
    }

    public DragManager getDragManager() {
        return dragManager;
    }

    public Collection<RateParameter> getRateParameters() {
        return petriNet.getRateParameters();
    }

    public void createNewRateParameter(String id, String expression) throws InvalidRateException {
        RateParameter rateParameter = new RateParameter(expression, id, id);
        HistoryItem historyItem = new AddPetriNetObject(rateParameter, petriNet);
        historyManager.addNewEdit(historyItem);
        petriNet.addRateParameter(rateParameter);
    }

    /**
     * Updates the rate parameter according to the id and expression
     * If they are the same as the old values it will not update them
     *
     * @param oldId registered id for the RateParameter in the petri net
     * @param newId new id to change the name to
     * @param newExpression new Expression to change to
     */
    public void updateRateParameter(String oldId, String newId, String newExpression) throws
            PetriNetComponentNotFoundException {
        RateParameter rateParameter = petriNet.getRateParameter(oldId);

        if (!oldId.equals(newId)) {
            changeRateParameterId(rateParameter, oldId, newId);
        }
        if (!rateParameter.getExpression().equals(newExpression)) {
            changeRateParameterRate(rateParameter, rateParameter.getExpression(), newExpression);
        }
    }

    private void changeRateParameterId(RateParameter rateParameter, String oldId, String newId) {
        HistoryItem historyItem = new RateParameterId(rateParameter, oldId, newId);
        historyManager.addNewEdit(historyItem);
        rateParameter.setId(newId);
        rateParameter.setName(newId);
    }

    private void changeRateParameterRate(RateParameter rateParameter, String oldRate, String newRate) {

        HistoryItem historyItem = new RateParameterValue(rateParameter, oldRate, newRate);
        historyManager.addNewEdit(historyItem);
        rateParameter.setExpression(newRate);
    }

    public boolean isUniqueName(String newName) {
        return placeNamer.isUniqueName(newName) && transitionNamer.isUniqueName(newName);
    }

    public void deleteRateParameter(String name) throws PetriNetComponentNotFoundException {
        RateParameter rateParameter = petriNet.getRateParameter(name);
        HistoryItem historyItem = new DeletePetriNetObject(rateParameter, petriNet);
        historyManager.addNewEdit(historyItem);
        petriNet.removeRateParameter(rateParameter);
    }
}
