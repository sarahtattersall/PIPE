package pipe.petrinet.adapters.valueAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanValueAdapter extends XmlAdapter<BooleanValueAdapter.AdaptedBoolean, Boolean> {

    @Override
    public Boolean unmarshal(AdaptedBoolean adaptedBoolean) throws Exception {
        return adaptedBoolean.value;
    }

    @Override
    public AdaptedBoolean marshal(Boolean aBoolean) throws Exception {
        AdaptedBoolean adapted = new AdaptedBoolean();
        adapted.value = aBoolean;
        return adapted;
    }

    public static class AdaptedBoolean {
        public boolean value;
    }
}
