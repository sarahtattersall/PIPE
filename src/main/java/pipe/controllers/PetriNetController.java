package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.gui.Animator;
import pipe.gui.ZoomController;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.DeletePetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.models.strategy.arc.ForwardsNormalStrategy;
import pipe.models.strategy.arc.InhibitorStrategy;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.models.visitor.TranslationVisitor;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class PetriNetController implements IController, Serializable {

    private final ZoomController zoomController = new ZoomController(100);
    private final HistoryManager historyManager;
    private final PetriNet petriNet;
    private final Set<PetriNetComponent> selectedComponents = new
            HashSet<PetriNetComponent>();
    private final ArcStrategy<Place, Transition> inhibitorStrategy = new InhibitorStrategy();
    private final ArcStrategy<Transition, Place> forwardNormalStrategy;
    private final ArcStrategy<Place, Transition> backwardsNormalStrategy;
    private int placeNumber = 0;
    private int transitionNumber = 0;
    private Token selectedToken;
    private Animator animator;


    public PetriNetController(PetriNet model, HistoryManager historyManager, Animator animator) {
        petriNet = model;
        this.animator = animator;
        if (model.getTokens().size() > 0) {
            selectedToken = model.getTokens().iterator().next();
        }
        this.historyManager = historyManager;
        forwardNormalStrategy = new ForwardsNormalStrategy(petriNet);
        backwardsNormalStrategy = new BackwardsNormalStrategy(petriNet);
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
     * Starts creating an arc from the source.
     *
     * @param source source model
     * @param target
     */
    public <S extends Connectable, T extends Connectable> void createNormalArc(S source, T target,
                                                                               Token currentToken) {
        buildArc(source, target, currentToken);
    }

    /**
     *
     * Creates a Normal Arc starting at source and ending at transition.
     *
     * Gives it an initial weighting of 1 x token
     *
     * @param source arc source
     * @param target arc targets
     * @param token  token to assign initial default weight to
     * @param <S>    Source type
     * @param <T>    Target type
     */
    //TODO: Work out how to avoid the cast?
    private <S extends Connectable, T extends Connectable> void buildArc(S source, T target, Token token) {
        Map<Token, String> tokens = new HashMap<Token, String>();
        tokens.put(token, "1");

        Arc<? extends Connectable, ? extends Connectable> arc = null;
        if (source.getClass().equals(Place.class) && target.getClass().equals(Transition.class)) {
            Place place = (Place) source;
            Transition transition = (Transition) target;
            arc = new Arc<Place, Transition>(place, transition, tokens, backwardsNormalStrategy);
        }else if (source.getClass().equals(Transition.class) && target.getClass().equals(Place.class)) {
            Place place = (Place) target;
            Transition transition = (Transition) source;
            arc = new Arc<Transition, Place>(transition, place, tokens, forwardNormalStrategy);
        }

        if (arc != null) {
            addArcToCurrentPetriNet(arc);
        }
    }

    private void addArcToCurrentPetriNet(Arc<? extends Connectable, ? extends Connectable> arc) {
        petriNet.addArc(arc);
    }


    /**
     * Start creating an inhibitor arc starting at the source
     *
     * @param source
     */
    public <S extends  Connectable, T extends  Connectable> void startCreatingInhibitorArc(S source, T target, Token currentToken) {
        if (source.getClass().equals(Place.class) && target.getClass().equals(Transition.class)) {
            Place place = (Place) source;
            Transition transition = (Transition) target;
            Arc<Place, Transition>  arc = new Arc<Place, Transition>(place, transition, new HashMap<Token, String>(), inhibitorStrategy);
            addArcToCurrentPetriNet(arc);
        }
    }

    /**
     * Creates unique petri net numbers for each tab
     *
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniquePlaceNumber() {
        int returnValue = placeNumber;
        placeNumber++;
        return returnValue;
    }

    /**
     * Creates unique petri net numbers for each tab
     *
     * @return A unique number for the petrinet in the current tab
     */
    public int getUniqueTransitionNumber() {
        int returnValue = transitionNumber;
        transitionNumber++;
        return returnValue;
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
        PetriNetComponentVisitor translationVisitor = new TranslationVisitor(translation, this);
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
            selectConnectable(place, selectionRectangle);
        }
        for (Transition transition : petriNet.getTransitions()) {
            selectConnectable(transition, selectionRectangle);
        }
        for (Arc arc : petriNet.getArcs()) {
            if (isArcSelected(arc, selectionRectangle) ||
                    selectedComponents.contains(arc.getSource()) ||
                    selectedComponents.contains(arc.getTarget())) {
                select(arc);
            }
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
     * @param component
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

    public void updateToken(String currentTokenName, String name, Boolean enabled, Color color) {
        petriNet.getToken(currentTokenName);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public PetriNet getPetriNet() {
        return petriNet;
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

    public <S extends Connectable, T extends Connectable> ArcController<S, T> getArcController(Arc<S, T> arc) {
        return new ArcController<S, T>(arc, historyManager);
    }

    public PlaceController getPlaceController(Place place) {
        return new PlaceController(place, historyManager);
    }

    public TransitionController getTransitionController(
            final Transition transition) {
        return new TransitionController(transition, historyManager);
    }

    //TODO: Should this be in the model???
    public void selectToken(Token token) {
        this.selectedToken = token;
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
}
