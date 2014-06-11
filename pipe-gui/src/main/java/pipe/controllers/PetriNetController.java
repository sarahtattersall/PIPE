package pipe.controllers;

import pipe.gui.PetriNetTab;
import pipe.historyActions.component.DeletePetriNetObject;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.naming.PlaceNamer;
import uk.ac.imperial.pipe.naming.TransitionNamer;
import uk.ac.imperial.pipe.naming.UniqueNamer;
import uk.ac.imperial.pipe.parsers.FunctionalResults;
import uk.ac.imperial.pipe.visitor.ClonePetriNet;
import uk.ac.imperial.pipe.visitor.TranslationVisitor;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

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

public class PetriNetController implements Serializable {

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

    /**
     * Listener for tool bar actions that create undoable actions
     */
    private final UndoableEditListener undoListener;

    /**
     * Tab that the Petri net is shown on
     */
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

    /**
     * Copy of the last saved version of the Petri net
     */
    private PetriNet lastSavedNet;

    /**
     * Set to true if the Petri net is in animation mode
     */
    private boolean animateMode = false;

    /**
     * Selection manager for selecting petri net components
     */
    private SelectionManager selectionManager;

    /**
     * Constructor
     * @param model underlying Petri net
     * @param undoListener undo listener for tool bar buttons undo actions
     * @param animator Petri net animator
     * @param copyPasteManager copy paste manager for the Petri net
     * @param zoomController zoom controller for the Petri net
     * @param petriNetTab tab this Petri net is displayed on
     */
    public PetriNetController(PetriNet model, UndoableEditListener undoListener, GUIAnimator animator,
                              CopyPasteManager copyPasteManager, ZoomController zoomController,
                              PetriNetTab petriNetTab) {
        petriNet = model;
        this.undoListener = undoListener;
        this.petriNetTab = petriNetTab;
        selectionManager = new SelectionManager(this);
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

    /**
     * @return A unique name for a transition in the current petri net
     */
    public String getUniqueTransitionName() {
        return transitionNamer.getName();
    }

    /**
     *
     * @param component
     * @return true if this component is selected on the canvas
     */
    public boolean isSelected(PetriNetComponent component) {
        return selectedComponents.contains(component);
    }

    /**
     * unselect the component on the canvas
     * @param component
     */
    public void deselect(PetriNetComponent component) {
        selectedComponents.remove(component);
    }

    /**
     * Deselect all canvas componentns
     */
    public void deselectAll() {
        selectedComponents.clear();
    }

    /**
     * Translates any components that are selected using a TranslationVisitor
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

    /**
     * Select the Petri net component on the canvas
     * @param component
     */
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
        int x = placeable.getX();
        int y = placeable.getY();
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
        Token token = new ColoredToken(name, color);
        petriNet.addToken(token);
    }

    /**
     *
     * @return all tokens in the Petri net
     */
    public Collection<Token> getNetTokens() {
        return petriNet.getTokens();
    }

    /**
     * Update the token with the specified name and color
     * @param currentTokenName
     * @param name
     * @param color
     * @throws PetriNetComponentNotFoundException
     */
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

    /**
     *
     * @return underlying Petri net model
     */
    //TODO: Shouldnt expose this!
    public PetriNet getPetriNet() {
        return petriNet;
    }

    /**
     * @param arc
     * @param <S>
     * @param <T>
     * @return controller for the model
     */
    public <S extends Connectable, T extends Connectable> ArcController<S, T> getArcController(Arc<S, T> arc) {
        return new ArcController<>(arc, this, undoListener);
    }

    /**
     *
     * @param place
     * @return controller for the place
     */
    public PlaceController getPlaceController(Place place) {
        return new PlaceController(place, undoListener);
    }

    /**
     *
     * @param annotation
     * @return controller for the annotation
     */
    public AnnotationController getAnnotationController(Annotation annotation) {
       return new AnnotationController(annotation, undoListener);
    }

    /**
     *
     * @param transition
     * @return controller for the transition
     */
    public TransitionController getTransitionController(final Transition transition) {
        return new TransitionController(transition, undoListener);
    }

    /**
     *
     * @param rateParameter
     * @return contrioller for the rate paramter
     * @throws PetriNetComponentNotFoundException
     */
    public RateParameterController getRateParameterController(final String rateParameter)
            throws PetriNetComponentNotFoundException {
        RateParameter parameter = petriNet.getComponent(rateParameter, RateParameter.class);
        return new RateParameterController(parameter, petriNet, undoListener);
    }

    /**
     * Selected token on the drop down menu
     * @param tokenName
     * @throws PetriNetComponentNotFoundException
     */
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

    /**
     * Copy all components that are selected
     */
    public void copySelection() {
        copyPasteManager.copy(selectedComponents);
    }

    /**
     *
     * @return true if a paste has been enabled
     */
    public boolean isCopyInProgress() {
        return copyPasteManager.pasteEnabled();
    }

    /**
     * Cancels the current paste
     */
    public void cancelPaste() {
        copyPasteManager.cancelPaste();
    }

    /**
     *
     * The selected token can then be used to add tokens to places
     *
     * @return the current token on the drop down menu
     */
    public String getSelectedToken() {
        return selectedToken;
    }

    /**
     *
     * @return the animator of the Petri net
     */
    public GUIAnimator getAnimator() {
        return animator;
    }

    /**
     *
     * @return the zoom controller of the Petri net
     */
    public ZoomController getZoomController() {
        return zoomController;
    }

    /**
     * Paste the copied items onto the Petri net
     */
    public void paste() {
        copyPasteManager.showPasteRectangle();
    }

    /**
     *
     * @return Petri net drag manager
     */
    public DragManager getDragManager() {
        return dragManager;
    }

    /**
     *
     * @return rate parameters in the Petri net
     */
    public Collection<RateParameter> getRateParameters() {
        return petriNet.getRateParameters();
    }

    /**
     *
     * @param id
     * @return true if this id does not exist inthe Petri net
     */
    public boolean isUniqueName(String id) {
        return placeNamer.isUniqueName(id) && transitionNamer.isUniqueName(id);
    }

    /**
     *
     * @return true if the Petri net has changed since it was last saved/loaded
     */
    public boolean hasChanged() {
        return !petriNet.equals(lastSavedNet);
    }

    /**
     * Take a clone of the Petri net
     */
    public void save() {
        lastSavedNet = ClonePetriNet.clone(petriNet);
    }

    /**
     *
     * @param expr
     * @return parsed functional expression in relation to the Petri nets current state
     */
    public FunctionalResults<Double> parseFunctionalExpression(String expr) {
        return petriNet.parseExpression(expr);
    }

    /**
     *
     * @return Petri nets undo manager
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }


    /**
     *
     * @return all selected components
     */
    public Set<PetriNetComponent> getSelectedComponents() {
        return selectedComponents;
    }

    /**
     *
     * @return Petri net undo listener
     */
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

    /**
     *
     * @return if the Petri net should be displayed in animation mode on the canvas
     */
    public boolean isInAnimationMode() {
        return animateMode;
    }

    /**
     *
     * @return Petri net selection manager
     */
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
}
