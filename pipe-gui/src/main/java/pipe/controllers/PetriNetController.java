package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.exceptions.PetriNetComponentException;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.*;
import pipe.historyActions.component.DeletePetriNetObject;
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
import pipe.naming.PlaceNamer;
import pipe.naming.TransitionNamer;
import pipe.naming.UniqueNamer;
import pipe.parsers.FunctionalResults;
import pipe.visitor.ClonePetriNet;
import pipe.visitor.TranslationVisitor;
import pipe.visitor.component.PetriNetComponentVisitor;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class PetriNetController implements IController, Serializable {

    /**
     * Responsible for zooming of the current Petri net
     */
    private final ZoomController zoomController;

    /**
     * Responsible for handling undo/redo
     */
    private final UndoManager undoManager = new UndoManager();

    /**
     * Petri net being displayed
     */
    private final PetriNet petriNet;

    private final UndoableEditListener undoListener;

    private final PetriNetTab petriNetTab;

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
    private final UniqueNamer placeNamer;

    /**
     * Responsible for creating unique transition names
     */
    private final UniqueNamer transitionNamer;

    /**
     * Token id that is currently selected in the drop down
     */
    private String selectedToken;

    /**
     * Animator class for animating tokens in the petri net
     */
    private GUIAnimator animator;


    /**
     * Drag manager for dragging selected objects
     */
    private DragManager dragManager = new DragManager(this);

    /**
     * Name of file the Petri net is saved to. Empty string if it has not yet been saved/loaded
     * from file
     */
    private String fileName = "";

    private PetriNet lastSavedNet;

    /**
     * Set to true if the Petri net is in animation mode
     */
    private boolean animateMode = false;

    public PetriNetController(PetriNet model, UndoableEditListener undoListener, GUIAnimator animator,
                              CopyPasteManager copyPasteManager, ZoomController zoomController,
                              PetriNetTab petriNetTab) {
        petriNet = model;
        this.undoListener = undoListener;
        this.petriNetTab = petriNetTab;
        lastSavedNet = ClonePetriNet.clone(model);
        this.zoomController = zoomController;
        this.animator = animator;
        this.copyPasteManager = copyPasteManager;
        if (model.getTokens().size() > 0) {
            selectedToken = model.getTokens().iterator().next().getId();
        }
        placeNamer = new PlaceNamer(model);
        transitionNamer = new TransitionNamer(model);
    }

    /**
     * @return Tab this controller is associated with
     */
    public PetriNetTab getPetriNetTab() {
        return petriNetTab;
    }

    /**
     * @return A unique name for a place in the current petri net
     */
    public String getUniquePlaceName() {
        return placeNamer.getName();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    /**
     * Translates any components that are selected using a {@link pipe.visitor.TranslationVisitor}
     *
     * @param translation translation distance
     */
    public void translateSelected(Point translation) throws PetriNetComponentException {
        PetriNetComponentVisitor translationVisitor = new TranslationVisitor(translation, selectedComponents);
        for (PetriNetComponent component : selectedComponents) {
            if (component.isDraggable()) {
                component.accept(translationVisitor);
            }
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
                for (ArcPoint arcPoint : arc.getArcPoints()) {
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
     * @param arc                arc to test to see if it is selected
     * @param selectionRectangle bounds of selection on screen
     * @return if selectionRectangle intersects the path
     */
    private boolean isArcSelected(Arc<? extends Connectable, ? extends Connectable> arc, Rectangle selectionRectangle) {
        GeneralPath path = createStraightPath(arc);
        return path.intersects(selectionRectangle);
    }

    /**
     * Creates an arc with a straight path arc
     *
     * @param arc
     * @return Straight path for arc, ignoring Bezier curves
     */
    private GeneralPath createStraightPath(Arc<? extends Connectable, ? extends Connectable> arc) {
        GeneralPath path = new GeneralPath();

        Collection<ArcPoint> arcPoints = arc.getArcPoints();
        int index = 0;
        for (ArcPoint arcPoint : arcPoints) {
            if (index == 0) {
                path.moveTo(arcPoint.getX(), arcPoint.getY());
            } else {
                Point2D point = arcPoint.getPoint();
                path.lineTo(point.getX(), point.getY());

            }
            index++;
        }
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
    public List<UndoableEdit> deleteSelection() throws PetriNetComponentException {
        List<UndoableEdit> edits = new LinkedList<>();
        for (PetriNetComponent component : selectedComponents) {
            edits.add(deleteComponent(component));
        }
        selectedComponents.clear();
        return edits;
    }

    /**
     * Deletes a component and returns the AbstractUndoableEdit in order
     * to redo the action
     *
     * @param component
     * @return AbstractUndoableEdit created for deleting the component
     */
    private UndoableEdit deleteComponent(PetriNetComponent component) throws PetriNetComponentException {
        petriNet.remove(component);
        return new DeletePetriNetObject(component, petriNet);
    }

    /**
     * Deletes single component, starts a newEdit for history manager
     *
     * @param component to delete
     * @return AbstractUndableEdit created
     */
    public UndoableEdit delete(PetriNetComponent component) throws PetriNetComponentException {
        return deleteComponent(component);
    }

    /**
     * Adds a new token to the petrinet
     *
     * @param name
     * @param color
     */
    public void createNewToken(String name, Color color) {
        Token token = new Token(name, color);
        petriNet.addToken(token);
    }

    public Collection<Token> getNetTokens() {
        return petriNet.getTokens();
    }

    public void updateToken(String currentTokenName, String name, Color color)
            throws PetriNetComponentNotFoundException {
        Token token = petriNet.getComponent(currentTokenName, Token.class);
        if (!token.getId().equals(name)) {
            token.setId(name);
        }
        if (!token.getColor().equals(color)) {
            token.setColor(color);
        }
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public <S extends Connectable, T extends Connectable> ArcController<S, T> getArcController(Arc<S, T> arc) {
        return new ArcController<>(arc, this, undoListener);
    }

    public PlaceController getPlaceController(Place place) {
        return new PlaceController(place, undoListener);
    }

    public AnnotationController getAnnotationController(Annotation annotation) {
       return new AnnotationController(annotation, undoListener);
    }

    public TransitionController getTransitionController(final Transition transition) {
        return new TransitionController(transition, undoListener);
    }

    public void selectToken(String tokenName) throws PetriNetComponentNotFoundException {
        selectedToken = tokenName;
    }

    /**
     * @param name token name to find
     * @return Token from PetriNet
     * @throw RuntimeException if the token does not exist
     */
    public Token getToken(String name) throws PetriNetComponentNotFoundException {
        return petriNet.getComponent(name, Token.class);
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

    public String getSelectedToken() {
        return selectedToken;
    }

    public GUIAnimator getAnimator() {
        return animator;
    }

    public ZoomController getZoomController() {
        return zoomController;
    }

    public void paste() {
        copyPasteManager.showPasteRectangle();
    }

    public DragManager getDragManager() {
        return dragManager;
    }

    public Collection<RateParameter> getRateParameters() {
        return petriNet.getRateParameters();
    }

    public boolean isUniqueName(String newName) {
        return placeNamer.isUniqueName(newName) && transitionNamer.isUniqueName(newName);
    }

    public boolean hasChanged() {
        return !petriNet.equals(lastSavedNet);
    }

    public void save() {
        lastSavedNet = ClonePetriNet.clone(petriNet);
    }

    public FunctionalResults<Double> parseFunctionalExpression(String expr) {
        return petriNet.parseExpression(expr);
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }


    public Set<PetriNetComponent> getSelectedComponents() {
        return selectedComponents;
    }

    public UndoableEditListener getUndoListener() {
        return undoListener;
    }

    /**
     *
     * Toggles aniamtion from false -> true or true -> false
     * @return new mode
     */
    public boolean toggleAnimation() {
        animateMode = !animateMode;
        return animateMode;
    }

    public boolean isInAnimationMode() {
        return animateMode;
    }
}
