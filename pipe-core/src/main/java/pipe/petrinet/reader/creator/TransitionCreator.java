package pipe.petrinet.reader.creator;

import org.w3c.dom.Element;
import pipe.models.component.Transition;

import java.util.HashMap;
import java.util.Map;

public class TransitionCreator implements ComponentCreator<Transition> {

//    private Map<String, RateParameter> rates = new HashMap<String, RateParameter>();

//    public void setRates(Map<String, RateParameter> rates) {
//        this.rates = rates;
//    }

    public Transition create(Element element) {
        double x = CreatorUtils.zeroOrValueOf(element.getAttribute("positionX"));

        double y = CreatorUtils.zeroOrValueOf(element.getAttribute("positionY"));

        String id = element.getAttribute("id");
        String name = element.getAttribute("name");

        double nameXOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("nameOffsetX"));
        double nameYOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("nameOffsetY"));

        int angle = CreatorUtils.zeroOrValueOfInt(element.getAttribute("angle"));
        int priority = CreatorUtils.zeroOrValueOfInt(element.getAttribute("priority"));

        String rate = element.getAttribute("rate");
        if (rate.isEmpty()) {
            rate = "1.0";
        }

        boolean timedTransition = CreatorUtils.falseOrValueOf(element.getAttribute("timed"));
        boolean infiniteServer = CreatorUtils.falseOrValueOf(element.getAttribute("infiniteServer"));

//        RateParameter rateParameter = getRateParameter(element.getAttribute("parameter"));

        Transition transition = createTransitionModel(id, name, rate, priority,
                                                      nameXOffset, nameYOffset,
                                                      angle, x, y, infiniteServer,
                                                      timedTransition);

        return transition;
    }
//
//    private RateParameter getRateParameter(String parameter) {
//        return rates.get(parameter);
//    }

    private Transition createTransitionModel(String id, String name,
                                             String rate,
                                             int priority, double nameXOffset,
                                             double nameYOffset, int angle,
                                             double x,
                                             double y, boolean infiniteServer,
                                             boolean timedTransition
                                             ) {
        Transition transition = new Transition(id, name, rate, priority);
        transition.setNameXOffset(nameXOffset);
        transition.setNameYOffset(nameYOffset);
        transition.setAngle(angle);
        transition.setX(x);
        transition.setY(y);
        transition.setInfiniteServer(infiniteServer);
        transition.setTimed(timedTransition);
//        transition.setRateParameter(rateParameter);

        return transition;
    }
}
