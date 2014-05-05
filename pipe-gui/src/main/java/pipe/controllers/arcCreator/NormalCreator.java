package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.component.Connectable;
import pipe.models.component.arc.*;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalCreator implements ArcActionCreator {

    private final PipeApplicationController controller;

    public NormalCreator(PipeApplicationController controller) {

        this.controller = controller;
    }

    private Map<Token, String> getInitialTokenWeights() {

        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();

        Map<Token, String> tokens = new HashMap<>();
        tokens.put(token, "1");

        return tokens;
    }

    @Override
    public InboundArc createInboundArc(Place source, Transition target, List<ArcPoint> arcPoints) {
        Map<Token, String> weights = getInitialTokenWeights();
        InboundArc inboundArc = new InboundNormalArc(source, target, weights);
        inboundArc.addIntermediatePoints(arcPoints);
        return inboundArc;
    }

    @Override
    public OutboundArc createOutboundArc(Place target, Transition source, List<ArcPoint> arcPoints) {
        Map<Token, String> weights = getInitialTokenWeights();
        OutboundArc outboundArc = new OutboundNormalArc(source, target, weights);
        outboundArc.addIntermediatePoints(arcPoints);
        return outboundArc;
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {

        return !source.getClass().equals(target.getClass());
    }

}
