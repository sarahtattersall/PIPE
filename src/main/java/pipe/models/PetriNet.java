package pipe.models;

import parser.ExprEvaluator;
import pipe.common.dataLayer.StateGroup;
import pipe.gui.ApplicationSettings;
import pipe.models.component.*;
import pipe.models.interfaces.IObserver;
import pipe.models.visitor.PetriNetComponentAddVisitor;
import pipe.models.visitor.PetriNetComponentRemovalVisitor;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.utilities.math.IncidenceMatrix;
import pipe.utilities.math.RandomNumberGenerator;
import pipe.views.*;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import java.util.*;

public class PetriNet extends Observable implements IObserver {
    public String _pnmlName = "";
    private boolean _validated = false;
    private ArrayList _changeArrayList;

    private Set<Transition> transitions = new HashSet<Transition>();
    private Set<Place> places = new HashSet<Place>();
    private Set<Token> tokens = new HashSet<Token>();
    private Set<Arc> arcs = new HashSet<Arc>();
    private Set<Annotation> annotations = new HashSet<Annotation>();
    private Set<RateParameter> rates = new HashSet<RateParameter>();
    private Set<StateGroup> stateGroups = new HashSet<StateGroup>();

    //TODO: CYCLIC DEPENDENCY BETWEEN CREATING THIS AND PETRINET/
    private final PetriNetComponentVisitor deleteVisitor = new PetriNetComponentRemovalVisitor(this);
    private PetriNetComponentVisitor addVisitor = new PetriNetComponentAddVisitor(this);

    public String getPnmlName() {
        return _pnmlName;
    }

    public void setPnmlName(String pnmlName) {
        _pnmlName = pnmlName;
    }

    public boolean isValidated() {
        return _validated;
    }

    public void setValidated(boolean validated) {
        _validated = validated;
    }

    public void resetPNML() {
        _pnmlName = null;
    }

    public void addPlace(Place place) {
        places.add(place);
        place.registerObserver(this);
        notifyObservers();
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
        transition.registerObserver(this);
        notifyObservers();
    }

    public void addArc(Arc arc) {
        arcs.add(arc);
        arc.registerObserver(this);
        notifyObservers();
    }

    public void addToken(Token token) {
        tokens.add(token);
        token.registerObserver(this);
        notifyObservers();
    }

    public Collection<Place> getPlaces() {
        return places;
    }

    public void addRate(RateParameter parameter) {
        rates.add(parameter);
        notifyObservers();
    }

    public Collection<RateParameter> getRateParameters() {
        return rates;
    }

    public void addAnnotaiton(Annotation annotation) {
        annotations.add(annotation);
        annotation.registerObserver(this);
        notifyObservers();
    }

    public Collection<Annotation> getAnnotations() {
        return annotations;
    }

    public void addStateGroup(StateGroup group) {
        stateGroups.add(group);
        notifyObservers();
    }

    public Collection<StateGroup> getStateGroups() {
        return stateGroups;
    }

    public Collection<Transition> getTransitions() {
        return transitions;
    }

    public Collection<Arc> getArcs() {
        return arcs;
    }

    public Collection<Token> getTokens() {
        return tokens;
    }

    public void removePlace(Place place) {
        this.places.remove(place);
        notifyObservers();
    }

    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        notifyObservers();
    }

    public void removeArc(Arc arc) {
        this.arcs.remove(arc);
        removeArcFromSourceAndTarget(arc);
        notifyObservers();
    }

    /**
     * Removes the arc from the source and target inbound/outbound Collections
     */
    private void removeArcFromSourceAndTarget(Arc arc) {
        Connectable source = arc.getSource();
        Connectable target = arc.getTarget();
        source.removeOutboundArc(arc);
        target.removeInboundArc(arc);
    }

    public void remove(PetriNetComponent component) {
        component.accept(deleteVisitor);
        notifyObservers();
    }

    public void removeToken(Token token) {
        tokens.remove(token);
    }

    public void removeRateParameter(RateParameter parameter) {
        rates.remove(parameter);
    }

    public void removeStateGroup(StateGroup group) {
        stateGroups.remove(group);
    }

    public void removeAnnotaiton(Annotation annotation) {
        annotations.remove(annotation);
    }

    public Token getToken(String tokenId) {
        for (Token token : tokens) {
            if (token.getId().equals(tokenId)) {
                return token;
            }
        }
        throw new RuntimeException("No token " + tokenId + " exists in petrinet.");
    }

    public void add(PetriNetComponent component) {
        component.accept(addVisitor);
        notifyObservers();
    }


    //TODO: IS THIS WHAT IT DOES?

    /**
     * A Transition is enabled if all its input places are marked with at least one token
     * This method calculates the minimium number of tokens needed in order for a transition to be enabeld
     *
     * @param transition
     * @return
     * @throws Exception
     */
    public int getEnablingDegree(Transition transition) {

        int enablingDegree = Integer.MAX_VALUE;

        ExprEvaluator evaluator = new ExprEvaluator(this);
        for (Arc arc : transition.inboundArcs()) {
            Place place = (Place) arc.getSource();
            Map<Token, String> arcWeights = arc.getTokenWeights();
            for (Map.Entry<Token, String> entry : arcWeights.entrySet()) {
                Token arcToken = entry.getKey();
                String arcTokenExpression = entry.getValue();

                int placeTokenCount = place.getTokenCount(arcToken);
                int requiredTokenCount = evaluator.parseAndEvalExpr(arcTokenExpression, arcToken.getId());

                if (requiredTokenCount == 0) {
                    enablingDegree = 0;
                } else {
                    //TODO: WHY DIVIDE?
                    int currentDegree = (int) Math.floor(placeTokenCount / requiredTokenCount);
                    if (currentDegree < enablingDegree) {
                        enablingDegree = currentDegree;
                    }

                }
            }
        }
        return enablingDegree;
    }

    @Override
    public void update() {
        notifyObservers();
    }

    /**
     * Calculates weights of connections from places to transitions for given token
     *
     * @param token
     * @throws Exception
     */
    public IncidenceMatrix getBackwardsIncidenceMatrix(Token token) {
        IncidenceMatrix backwardsIncidenceMatrix = new IncidenceMatrix();
        for (Arc arc : arcs) {
            Connectable target = arc.getTarget();
            Connectable source = arc.getSource();
            if (target instanceof Transition) {
                Transition transition = (Transition) target;
                if (source instanceof Place) {
                    Place place = (Place) source;
                    int enablingDegree = transition.isInfiniteServer() ? getEnablingDegree(transition) : 0;


                    String expression = arc.getWeightForToken(token);
                    ExprEvaluator paser = new ExprEvaluator(this);
                    Integer weight = paser.parseAndEvalExpr(expression, token.getId());
                    if (weight == 0) {  // Ie at least one token to pass
                        weight = 1;
                    }
                    int totalWeight = transition.isInfiniteServer() ? weight * enablingDegree : weight;
                    backwardsIncidenceMatrix.put(place, transition, totalWeight);
                }
            }
        }
        return backwardsIncidenceMatrix;
    }

    /**
     * Calculates weights of connections from transitions to places for given token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public IncidenceMatrix getForwardsIncidenceMatrix(Token token) {

        IncidenceMatrix forwardsIncidenceMatrix = new IncidenceMatrix();
        for (Arc arc : arcs) {
            Connectable target = arc.getTarget();
            Connectable source = arc.getSource();

            if (target instanceof Place) {
                Place place = (Place) target;
                if (source instanceof Transition) {
                    Transition transition = (Transition) source;

                    //TODO: Broken transitions
                    String expression = arc.getWeightForToken(token);

                    ExprEvaluator paser = new ExprEvaluator(this);

                    Integer weight = paser.parseAndEvalExpr(expression, token.getId());
                    if (weight == 0) {  // Ie at least one token to pass
                        weight = 1;
                    }
                    forwardsIncidenceMatrix.put(place, transition, weight);
                }
            }
        }
        return forwardsIncidenceMatrix;
    }

    /**
     * @param backwards
     * @return all transitions that can be enabled
     * @throws Exception
     */
    public Collection<Transition> getEnabledTransitions(boolean backwards) {
        boolean hasTimed = false;
        boolean hasImmediate = false;
        int maxPriority = 0;
        final LinkedList<Transition> enabledTransitions = new LinkedList<Transition>();

        for (Transition transition : getTransitions()) {
            boolean enabledForAllPlaces = true;
            for (Arc arc : transition.inboundArcs()) {
                //TODO: Avoid the cast
                Place place = (Place) arc.getSource();
                enabledForAllPlaces &= allPlaceTokensEnabled(backwards, transition, arc, place);
            }

            if (enabledForAllPlaces) {
                enabledTransitions.add(transition);

                // we look for the highest priority of the enabled transitions
                if (transition.isTimed()) {
                    hasTimed = true;
                } else {
                    hasImmediate = true;
                    if (transition.getPriority() > maxPriority) {
                        maxPriority = transition.getPriority();
                    }
                }
            }
        }

        // Now make sure that if any of the enabled transitions are immediate
        // transitions, only they can fire as this must then be a vanishing
        // state. That is:
        // - disable the immediate transitions with lower priority.
        // - disable all timed transitions if there is an immediate transition
        // enabled.
        Iterator<Transition> enabledTransitionIter = enabledTransitions.iterator();
        while (enabledTransitionIter.hasNext())

        {
            Transition enabledTransition = enabledTransitionIter.next();
            if ((!enabledTransition.isTimed() && enabledTransition.getPriority() < maxPriority) ||
                    (hasTimed && hasImmediate && enabledTransition.isTimed())) {
                enabledTransitionIter.remove();
            }
        }

        return enabledTransitions;
    }

    /**
     * @param backwards
     * @param transition
     * @param place
     * @return true if every token in the place enables the transition
     * @throws Exception
     */
    private boolean allPlaceTokensEnabled(boolean backwards,
                                          Transition transition,
                                          Arc arc,
                                          Place place) {
        int totalMarkings = 0;
        int totalIPlus = 0;
        int totalIMinus = 0;
        for (Token token : arc.getTokenWeights().keySet()) {
            int tokenCount = place.getTokenCount(token);

            IncidenceMatrix forwardsIncidenceMatrix = getForwardsIncidenceMatrix(token);
            IncidenceMatrix backwardsIncidenceMatrix;

            //TODO: WHAT IS THIS LOGIC?
            if (backwards) {
                backwardsIncidenceMatrix = forwardsIncidenceMatrix;
            } else {
                backwardsIncidenceMatrix = getBackwardsIncidenceMatrix(token);
            }
            //TODO: INHIBITION

            if (tokenCount < backwardsIncidenceMatrix.get(place, transition) && tokenCount != -1) {
                return false;
            }

            // Capacities
            totalMarkings += tokenCount;
            totalIPlus += forwardsIncidenceMatrix.get(place, transition);
            totalIMinus += backwardsIncidenceMatrix.get(place, transition);

            if (place.getCapacity() > 0 &&
                    (totalMarkings + totalIPlus - totalIMinus > place.getCapacity())) {
                return false;
            }

            //TODO: INHIBITOR
        }
        return !place.getTokenCounts().isEmpty();
    }

    public Transition getRandomTransition() {

        Collection<Transition> enabledTransitions = getEnabledTransitions(false);
        if (enabledTransitions.isEmpty()) {
            throw new RuntimeException("Error - no transitions to fire!");
        }

        Random random = new Random();
        int index = random.nextInt(enabledTransitions.size());

        Iterator<Transition> iter = enabledTransitions.iterator();
        Transition transition = iter.next();
        for (int i = 1; i < index; i++) {
            transition = iter.next();
        }
        return transition;
    }

    /**
     * Removes tokens from places into the transition
     * Adds tokens to the places out of the transition according to the arc weight
     * Disables fired transition
     * @param transition
     */
    public void fireTransition(Transition transition) {
        Collection<Transition> enabledTransitions = getEnabledTransitions(false);
        if (enabledTransitions.contains(transition)) {
            //Decrement previous places
            for (Arc arc : transition.inboundArcs()) {
                Place place = (Place) arc.getSource();
                for (Token token : arc.getTokenWeights().keySet()) {
                    IncidenceMatrix matrix = getBackwardsIncidenceMatrix(token);
                    int oldCount = place.getTokenCount(token);
                    int newCount = oldCount - matrix.get(place, transition);
                    place.setTokenCount(token, newCount);
                }
            }

            //Increment new places
            System.out.println("TRANSITION " + transition.getName() + " OUTBOUDN ARCS: " + transition.outboundArcs().size());
            for (Arc arc : transition.outboundArcs()) {
                Place place = (Place) arc.getTarget();
                for (Token token : arc.getTokenWeights().keySet()) {
                    IncidenceMatrix matrix = getForwardsIncidenceMatrix(token);
                    int oldCount = place.getTokenCount(token);
                    int newCount = oldCount + matrix.get(place, transition);
                    place.setTokenCount(token, newCount);
                }
            }
        }
        transition.disable();
    }

}
