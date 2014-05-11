package pipe.visitor;

import pipe.exceptions.InvalidRateException;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.*;
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
    private final Map<String, Place> places = new HashMap<>();
    private final Map<String, Transition> transitions = new HashMap<>();
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

        for (InboundArc arc : petriNet.getInboundArcs()) {
            visit(arc);
        }



        for (OutboundArc arc : petriNet.getOutboundArcs()) {
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

    public void visit(InboundArc arc) {
        Place source = places.get(arc.getSource().getId());
        Transition target = transitions.get(arc.getTarget().getId());
        InboundArc newArc;
        switch (arc.getType()) {
            case INHIBITOR:
                newArc = new InboundInhibitorArc(source, target);
                break;
            default:
                newArc = new InboundNormalArc(source, target, arc.getTokenWeights());
        }
        List<ArcPoint> arcPoints = arc.getArcPoints();
        for (int i = 1; i < arcPoints.size() -1; i++) {
            newArc.addIntermediatePoint(arcPoints.get(i));
        }
        newArc.setId(arc.getId());
        newPetriNet.addArc(newArc);
    }


    public void visit(OutboundArc arc) {
        Place target = places.get(arc.getTarget().getId());
        Transition source = transitions.get(arc.getSource().getId());

        OutboundArc newArc = new OutboundNormalArc(source, target, arc.getTokenWeights());
        List<ArcPoint> arcPoints = arc.getArcPoints();
        for (int i = 1; i < arcPoints.size() -1; i++) {
            newArc.addIntermediatePoint(arcPoints.get(i));
        }
        newArc.setId(arc.getId());
        newPetriNet.addArc(newArc);
    }

    public void visit(Place place) {
        Place newPlace = new Place(place);
        for (Map.Entry<String, Integer> entry : place.getTokenCounts().entrySet()) {
            newPlace.setTokenCount(entry.getKey(), entry.getValue());
        }
        newPetriNet.addPlace(newPlace);
        places.put(place.getId(), newPlace);
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
        transitions.put(transition.getId(), newTransition);
        newPetriNet.addTransition(newTransition);
    }
}
