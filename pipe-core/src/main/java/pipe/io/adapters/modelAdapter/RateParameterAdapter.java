package pipe.io.adapters.modelAdapter;

import pipe.io.adapters.model.AdaptedRateParameter;
import pipe.models.component.rate.RateParameter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RateParameterAdapter extends XmlAdapter<AdaptedRateParameter, RateParameter> {
    @Override
    public RateParameter unmarshal(AdaptedRateParameter adaptedRateParameter) {
        return new RateParameter(adaptedRateParameter.getExpression(), adaptedRateParameter.getId(), adaptedRateParameter.getName());
    }

    @Override
    public AdaptedRateParameter marshal(RateParameter rateParameter)  {
        AdaptedRateParameter adaptedRateParameter = new AdaptedRateParameter();
        adaptedRateParameter.setExpression(rateParameter.getExpression());
        adaptedRateParameter.setId(rateParameter.getId());
        adaptedRateParameter.setName(rateParameter.getName());
        return adaptedRateParameter;
    }
}
