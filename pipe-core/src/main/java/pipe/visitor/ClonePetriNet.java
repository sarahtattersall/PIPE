package pipe.visitor;

import pipe.exceptions.InvalidRateException;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.rate.RateType;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClonePetriNet {
    private final PetriNet petriNet;
    private final PetriNet newPetriNet;
    private final Map<String, RateParameter> rateParameters = new HashMap<>();
    private final Map<String, Connectable> connectables = new HashMap<>();
    private final Map<String, Token> newTokens = new HashMap<>();

    private ClonePetriNet (PetriNet petriNet) {
        this.petriNet = petriNet;
        newPetriNet = new PetriNet();
    }

    private PetriNet clonePetriNet() {
        for (Token token : petriNet.getTokens()) {
            visit(token);
        }

        for (RateParameter rateParameter : petriNet.getRateParameters()) {
            visit(rateParameter);
        }

        for (Annotation annotation : petriNet.getAnnotations()) {
            visit(annotation);
        }

        for (Place place : petriNet.getPlaces()) {
            visit(place);
        }

        for (Transition transition : petriNet.getTransitions()) {
            visit(transition);
        }

        for (Arc<? extends Connectable, ? extends Connectable> arc : petriNet.getArcs()) {
            visit(arc);
        }
        return newPetriNet;
    }

    public static PetriNet clone(PetriNet petriNet) {
        ClonePetriNet clone = new ClonePetriNet(petriNet);
        return clone.clonePetriNet();
    }

    public void visit(Annotation annotation) {
        Annotation newAnnotation =  new Annotation(annotation);
        newPetriNet.addAnnotation(newAnnotation);

    }

    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
        Connectable source = connectables.get(arc.getSource().getId());
        Connectable target = connectables.get(arc.getTarget().getId());
        Arc<? extends Connectable, ? extends Connectable> newArc = new Arc<>(source, target, arc.getTokenWeights(), arc.getType());
        List<ArcPoint> arcPoints = arc.getArcPoints();
        for (int i = 1; i < arcPoints.size() -1; i++) {
            newArc.addIntermediatePoint(arcPoints.get(i));
        }
        newArc.setId(arc.getId());
        newPetriNet.addArc(newArc);
    }

    public void visit(Place place) {
        Place newPlace = new Place(place);
        for (Map.Entry<Token, Integer> entry : place.getTokenCounts().entrySet()) {
            newPlace.setTokenCount(newTokens.get(entry.getKey().getId()), entry.getValue());
        }
        newPetriNet.addPlace(newPlace);
        connectables.put(place.getId(), place);
    }

    public void visit(RateParameter rate) {
       RateParameter rateParameter = new RateParameter(rate);
        try {
            newPetriNet.addRateParameter(rateParameter);
            rateParameters.put(rateParameter.getId(), rateParameter);
        } catch (InvalidRateException ignored) {
        }

    }

    public void visit(Token token) {
        Token newToken = new Token(token);
        newTokens.put(token.getId(), newToken);
        newPetriNet.addToken(newToken);
    }

    public void visit(Transition transition) {
        Transition newTransition = new Transition(transition);
        if (transition.getRate().getRateType().equals(RateType.RATE_PARAMETER)) {
            RateParameter rateParameter = (RateParameter) transition.getRate();
            newTransition.setRate(rateParameters.get(rateParameter.getId()));
        }
        connectables.put(transition.getId(), transition);
        newPetriNet.addTransition(transition);
    }
}
