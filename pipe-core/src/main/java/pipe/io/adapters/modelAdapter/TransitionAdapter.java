package pipe.io.adapters.modelAdapter;

import pipe.io.adapters.model.AdaptedConnectable;
import pipe.io.adapters.model.AdaptedTransition;
import pipe.io.adapters.model.NameDetails;
import pipe.io.adapters.utils.ConnectableUtils;
import pipe.models.component.rate.NormalRate;
import pipe.models.component.rate.Rate;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.transition.Transition;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class TransitionAdapter extends XmlAdapter<AdaptedTransition, Transition> {
    private final Map<String, Transition> transitions;
    private final Map<String, RateParameter> rateParameters;

    /**
     * Empty contructor needed for marshelling. Since the method to marshell does not actually
     * use these fields it's ok to initialise them as empty/null.
     */
    public TransitionAdapter() {
        transitions = new HashMap<String, Transition>();
        rateParameters = new HashMap<String, RateParameter>();
    }

    public TransitionAdapter(Map<String, Transition> transitions, Map<String, RateParameter> rateParameters) {

        this.transitions = transitions;
        this.rateParameters = rateParameters;
    }

    @Override
    public Transition unmarshal(AdaptedTransition adaptedTransition) {
        NameDetails nameDetails = adaptedTransition.getName();
        Transition transition = new Transition(adaptedTransition.getId(), nameDetails.getName());
        ConnectableUtils.setConntactableNameOffset(transition, adaptedTransition);
        ConnectableUtils.setConnectablePosition(transition, adaptedTransition);
        transition.setAngle(adaptedTransition.getAngle());
        transition.setPriority(adaptedTransition.getPriority());

        AdaptedTransition.ToolSpecific toolSpecific = adaptedTransition.getToolSpecific();
        Rate rate;
        if (toolSpecific == null) {
            rate = new NormalRate(adaptedTransition.getRate());
        } else {
            rate = rateParameters.get(toolSpecific.getRateDefinition());
        }
        transition.setRate(rate);
        transition.setTimed(adaptedTransition.getTimed());
        transition.setInfiniteServer(adaptedTransition.getInfiniteServer());
        transitions.put(transition.getId(), transition);
        return transition;
    }

    @Override
    public AdaptedTransition marshal(Transition transition) {
        AdaptedTransition adaptedTransition = new AdaptedTransition();
        ConnectableUtils.setAdaptedName(transition, adaptedTransition);

        adaptedTransition.setId(transition.getId());
        ConnectableUtils.setPosition(transition, adaptedTransition);
        adaptedTransition.setPriority(transition.getPriority());
        adaptedTransition.setAngle(transition.getAngle());
        adaptedTransition.setRate(transition.getRateExpr());
        adaptedTransition.setInfiniteServer(transition.isInfiniteServer());
        adaptedTransition.setTimed(transition.isTimed());

        Rate rate = transition.getRate();
        if (rate instanceof RateParameter) {
            RateParameter rateParameter = (RateParameter) rate;
            AdaptedTransition.ToolSpecific toolSpecific = new AdaptedTransition.ToolSpecific();
            toolSpecific.setRateDefinition(rateParameter.getId());
            adaptedTransition.setToolSpecific(toolSpecific);
        }

        return adaptedTransition;
    }
}
