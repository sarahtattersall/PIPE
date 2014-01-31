package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.gui.Animator;
import pipe.gui.CopyPasteManager;
import pipe.gui.DragManager;
import pipe.gui.ZoomController;
import pipe.historyActions.DeletePetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.PlaceablePetriNetComponent;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
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

    private final ZoomController zoomController;

    private final HistoryManager historyManager;

    private final PetriNet petriNet;

    private final Set<PetriNetComponent> selectedComponents = new HashSet<PetriNetComponent>();

    private final CopyPasteManager copyPasteManager;

    private final PetriNetComponentNamer placeNamer;

    private final PetriNetComponentNamer transitionNamer;

    private Token selectedToken;

    private Animator animator;

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

    public void setEndPoint(double x, double y, boolean shiftDown) {
        //        if (currentlyCreatingArc) {
        //            arc.setTarget(new TemporaryArcTarget(x, y));
        //
        //            petriNet.notifyObservers();
        //        }
    }

    public void addPoint(Point2D point, boolean curved) {
        //        arc.addIntermediatePoint(new ArcPoint(point, curved));
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
     * @param selectionRectangle
     */
    public void select(Rectangle selectionRectangle) {
        for (Place place : petriNet.getPlaces()) {
            selectPlaceable(place, selectionRectangle);
        }
        for (Transition transition : petriNet.getTransitions()) {
            selectPlaceable(transition, selectionRectangle);
        }
        for (Arc<? extends Connectable, ? extends Connectable> arc : petriNet.getArcs()) {
            if (isArcSelected(arc, selectionRectangle) ||
                    selectedComponents.contains(arc.getSource()) ||
                    selectedComponents.contains(arc.getTarget())) {
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
     * @param arc
     * @param selectionRectangle
     * @return if selectionRectangle intersects the path
     */
    private boolean isArcSelected(Arc arc, Rectangle selectionRectangle) {
        GeneralPath path = createStraightPath(arc);
        return path.intersects(selectionRectangle);
    }

    /**
     * @param arc
     * @return Straight path for arc, ignoring Bezier curves
     */
    private GeneralPath createStraightPath(Arc arc) {
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

    public void updateToken(String currentTokenName, String name, boolean enabled, Color color) {
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

    public void selectToken(String tokenName) {
        selectToken(getToken(tokenName));
    }

    public Token getToken(String tokenName) {
        return getTokenForName(tokenName);
    }

    /**
     * @param name token name to find
     * @return Token from PetriNet
     * @throw RuntimeException if the token does not exist
     */
    private Token getTokenForName(String name) {
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
}
