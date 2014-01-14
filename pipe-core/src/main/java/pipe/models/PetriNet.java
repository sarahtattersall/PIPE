package pipe.models;

import parser.ExprEvaluator;
import pipe.models.component.*;
import pipe.visitor.PetriNetComponentAddVisitor;
import pipe.visitor.PetriNetComponentRemovalVisitor;
import pipe.visitor.PetriNetComponentVisitor;
import pipe.math.IncidenceMatrix;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class PetriNet {


    //TODO: CYCLIC DEPENDENCY BETWEEN CREATING THIS AND PETRINET/
    private final PetriNetComponentVisitor deleteVisitor = new PetriNetComponentRemovalVisitor(this);
    public String _pnmlName = "";
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private boolean _validated = false;
    private Set<Transition> transitions = new HashSet<Transition>();
    private Set<Place> places = new HashSet<Place>();
    private Set<Token> tokens = new HashSet<Token>();
    private Set<Arc<? extends Connectable, ? extends Connectable>> arcs =
            new HashSet<Arc<? extends Connectable, ? extends Connectable>>();
    private Set<Annotation> annotations = new HashSet<Annotation>();
    //    private Set<RateParameter> rates = new HashSet<RateParameter>();
    //    private Set<StateGroup> stateGroups = new HashSet<StateGroup>();
    private PetriNetComponentVisitor addVisitor = new PetriNetComponentAddVisitor(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

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
        changeSupport.firePropertyChange("newPlace", null, place);
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
        changeSupport.firePropertyChange("newTransition", null, transition);
    }

    public void addArc(Arc<? extends Connectable, ? extends Connectable> arc) {
        arcs.add(arc);
        changeSupport.firePropertyChange("newArc", null, arc);
    }

    public void addToken(Token token) {
        tokens.add(token);
        changeSupport.firePropertyChange("newToken", null, token);
    }

    public Collection<Place> getPlaces() {
        return places;
    }

    //    public void addRate(RateParameter parameter) {
    //        rates.add(parameter);
    //        changeSupport.firePropertyChange("newRate", null, parameter);
    //    }

    //    public Collection<RateParameter> getRateParameters() {
    //        return rates;
    //    }

    public void addAnnotaiton(Annotation annotation) {
        annotations.add(annotation);
        changeSupport.firePropertyChange("addAnnotation", null, annotation);
    }

    public Collection<Annotation> getAnnotations() {
        return annotations;
    }

    //    public void addStateGroup(StateGroup group) {
    //        stateGroups.add(group);
    //        changeSupport.firePropertyChange("newStateGroup", null, group);
    //    }

    //    public Collection<StateGroup> getStateGroups() {
    //        return stateGroups;
    //    }

    public Collection<Arc<? extends Connectable, ? extends Connectable>> getArcs() {
        return arcs;
    }

    public Collection<Token> getTokens() {
        return tokens;
    }

    public void removePlace(Place place) {
        this.places.remove(place);
        for (Arc<Place, Transition> arc : outboundArcs(place)) {
            removeArc(arc);
        }
        changeSupport.firePropertyChange("deletePlace", place, null);
    }

    public void removeArc(Arc arc) {
        this.arcs.remove(arc);
        removeArcFromSourceAndTarget(arc);
        changeSupport.firePropertyChange("deleteArc", arc, null);
    }

    /**
     * Removes the arc from the source and target inbound/outbound Collections
     */
    private <S extends Connectable, T extends Connectable> void removeArcFromSourceAndTarget(Arc<S, T> arc) {
        Connectable source = arc.getSource();
        Connectable target = arc.getTarget();
    }

    /**
     * @param place
     * @return arcs that are outbound from place
     */
    private Collection<Arc<Place, Transition>> outboundArcs(Place place) {
        Collection<Arc<Place, Transition>> outbound = new LinkedList<Arc<Place, Transition>>();
        for (Arc<? extends Connectable, ? extends Connectable> arc : arcs) {
            if (arc.getSource().equals(place)) {
                outbound.add((Arc<Place, Transition>) arc);
            }
        }
        return outbound;
    }

    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        for (Arc<Transition, Place> arc : outboundArcs(transition)) {
            removeArc(arc);
        }
        changeSupport.firePropertyChange("deleteTransition", transition, null);
    }

    /**
     * @param transition
     * @return arcs that are outbound from transition
     */
    private Collection<Arc<Transition, Place>> outboundArcs(Transition transition) {
        Collection<Arc<Transition, Place>> outbound = new LinkedList<Arc<Transition, Place>>();
        for (Arc<? extends Connectable, ? extends Connectable> arc : arcs) {
            if (arc.getSource().equals(transition)) {
                outbound.add((Arc<Transition, Place>) arc);
            }
        }
        return outbound;
    }

    public void remove(PetriNetComponent component) {
        component.accept(deleteVisitor);
    }

    public void removeToken(Token token) {
        tokens.remove(token);
    }

    //    public void removeRateParameter(RateParameter parameter) {
    //        rates.remove(parameter);
    //    }

    //    public void removeStateGroup(StateGroup group) {
    //        stateGroups.remove(group);
    //    }

    public void removeAnnotaiton(Annotation annotation) {
        annotations.remove(annotation);
    }

    public Token getToken(String tokenId) {
        //TODO: Find an O(1) name to do this, perhaps Map?
        for (Token token : tokens) {
            if (token.getId().equals(tokenId)) {
                return token;
            }
        }
        throw new RuntimeException("No token " + tokenId + " exists in petrinet.");
    }

    public void add(PetriNetComponent component) {
        component.accept(addVisitor);
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
     * @param backwards firing backwards or forwards
     * @return all transitions that can be enabled
     */
    public Set<Transition> getEnabledTransitions(boolean backwards) {
        boolean hasTimed = false;
        boolean hasImmediate = false;
        int maxPriority = 0;
        final Set<Transition> enabledTransitions = new HashSet<Transition>();

        for (Transition transition : getTransitions()) {
            if (isEnabled(transition, backwards)) {
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

        //        Now make sure that if any of the enabled transitions are immediate
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

    public Collection<Transition> getTransitions() {
        return transitions;
    }

    /**
     * @param transition
     * @return true if transition is enabled
     */
    private boolean isEnabled(Transition transition, boolean backwards) {
        boolean enabledForArcs = true;
        for (Arc<Place, Transition> arc : inboundArcs(transition)) {
            enabledForArcs &= arc.canFire();
            //            enabledForArcs &= allPlaceTokensEnabled(backwards, arc);
        }

        for (Arc<Transition, Place> arc : outboundArcs(transition)) {
            //            Place place = arc.getTarget();
            enabledForArcs &= arc.canFire();
        }
        return enabledForArcs;
    }

    /**
     * @param transition
     * @return arcs that are inbound to transition
     */
    private Collection<Arc<Place, Transition>> inboundArcs(Transition transition) {
        Collection<Arc<Place, Transition>> outbound = new LinkedList<Arc<Place, Transition>>();
        for (Arc<? extends Connectable, ? extends Connectable> arc : arcs) {
            if (arc.getTarget().equals(transition)) {
                outbound.add((Arc<Place, Transition>) arc);
            }
        }
        return outbound;
    }

    /**
     * Removes tokens from places into the transition
     * Adds tokens to the places out of the transition according to the arc weight
     * Disables fired transition
     *
     * @param transition
     */
    public void fireTransition(Transition transition) {
        if (transition.isEnabled()) {
            //Decrement previous places
            for (Arc<Place, Transition> arc : inboundArcs(transition)) {
                Place place = arc.getSource();
                for (Token token : arc.getTokenWeights().keySet()) {
                    IncidenceMatrix matrix = getBackwardsIncidenceMatrix(token);
                    int currentCount = place.getTokenCount(token);
                    int newCount = currentCount - matrix.get(place, transition);
                    place.setTokenCount(token, newCount);
                }
            }

            //Increment new places
            for (Arc<Transition, Place> arc : outboundArcs(transition)) {
                Place place = arc.getTarget();
                for (Token token : arc.getTokenWeights().keySet()) {
                    IncidenceMatrix matrix = getForwardsIncidenceMatrix(token);
                    int currentCount = place.getTokenCount(token);
                    int newCount = currentCount + matrix.get(place, transition);
                    place.setTokenCount(token, newCount);
                }
            }
        }
        markEnabledTransitions();
    }

    /**
     * Calculates weights of connections from places to transitions for given token
     *
     * @param token calculates backwards incidence matrix for this token
     */
    public IncidenceMatrix getBackwardsIncidenceMatrix(Token token) {
        ExprEvaluator paser = new ExprEvaluator(this);
        IncidenceMatrix backwardsIncidenceMatrix = new IncidenceMatrix();
        for (Arc<? extends Connectable, ? extends Connectable> arc : arcs) {
            Connectable target = arc.getTarget();
            Connectable source = arc.getSource();
            if (target instanceof Transition) {
                Transition transition = (Transition) target;
                if (source instanceof Place) {
                    Place place = (Place) source;
                    int enablingDegree = transition.isInfiniteServer() ? getEnablingDegree(transition) : 0;


                    String expression = arc.getWeightForToken(token);
                    Integer weight = paser.parseAndEvalExpr(expression, token.getId());
                    int totalWeight = transition.isInfiniteServer() ? weight * enablingDegree : weight;
                    backwardsIncidenceMatrix.put(place, transition, totalWeight);
                }
            }
        }
        return backwardsIncidenceMatrix;
    }

    /**
     * A Transition is enabled if all its input places are marked with at least one token
     * This method calculates the minimium number of tokens needed in order for a transition to be enabeld
     *
     * The enabling degree is the number of times that a transition is enabled
     * @param transition
     * @return the transitions enabling degree
     */
    public int getEnablingDegree(Transition transition) {
        ExprEvaluator evaluator = new ExprEvaluator(this);
        int enablingDegree = Integer.MAX_VALUE;


        for (Arc<Place, Transition> arc : inboundArcs(transition)) {
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
                    int currentDegree = (int) Math.floor(placeTokenCount / requiredTokenCount);
                    if (currentDegree < enablingDegree) {
                        enablingDegree = currentDegree;
                    }

                }
            }
        }
        return enablingDegree;
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
        for (Arc<? extends Connectable, ? extends Connectable> arc : arcs) {
            Connectable target = arc.getTarget();
            Connectable source = arc.getSource();

            if (target instanceof Place) {
                Place place = (Place) target;
                if (source instanceof Transition) {
                    Transition transition = (Transition) source;

                    String expression = arc.getWeightForToken(token);

                    ExprEvaluator paser = new ExprEvaluator(this);

                    Integer weight = paser.parseAndEvalExpr(expression, token.getId());
                    forwardsIncidenceMatrix.put(place, transition, weight);
                }
            }
        }
        return forwardsIncidenceMatrix;
    }

    /**
     * Calculates enabled transitions and enables them.
     */
    public void markEnabledTransitions() {
        Set<Transition> enabledTransitions = getEnabledTransitions(false);
        for (Transition transition : transitions) {
            if (enabledTransitions.contains(transition)) {
                transition.enable();
            } else {
                transition.disable();
            }
        }
    }

    /**
     * Removes tokens from places out of the transition
     * Adds tokens to the places into the transition according to the arc weight
     * Enables fired transition
     *
     * @param transition
     */
    //TODO: NOT SURE IF BETTER TO JUST HAVE UNDO/REDO IN ANIMATION HISTORY? HAVE TO STORE ENTIRE PETRI
    //      NET STATES SO MAYBE NOT?
    public void fireTransitionBackwards(Transition transition) {
        //Increment previous places
        for (Arc<Place, Transition> arc : inboundArcs(transition)) {
            Place place = arc.getSource();
            for (Token token : arc.getTokenWeights().keySet()) {
                IncidenceMatrix matrix = getBackwardsIncidenceMatrix(token);
                int currentCount = place.getTokenCount(token);
                int newCount = currentCount + matrix.get(place, transition);
                place.setTokenCount(token, newCount);
            }
        }

        //Decrement new places
        for (Arc<Transition, Place> arc : outboundArcs(transition)) {
            Place place = arc.getTarget();
            for (Token token : arc.getTokenWeights().keySet()) {
                IncidenceMatrix matrix = getForwardsIncidenceMatrix(token);
                int oldCount = place.getTokenCount(token);
                int newCount = oldCount - matrix.get(place, transition);
                place.setTokenCount(token, newCount);
            }
        }
        markEnabledTransitions();
    }

}
