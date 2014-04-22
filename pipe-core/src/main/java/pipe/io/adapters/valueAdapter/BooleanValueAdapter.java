package pipe.io.adapters.valueAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanValueAdapter extends XmlAdapter<BooleanValueAdapter.AdaptedBoolean, Boolean> {

    @Override
    public Boolean unmarshal(AdaptedBoolean adaptedBoolean) {
        return adaptedBoolean.value;
    }

    @Override
    public AdaptedBoolean marshal(Boolean aBoolean) {
        AdaptedBoolean adapted = new AdaptedBoolean();
        adapted.value = aBoolean;
        return adapted;
    }

    public static class AdaptedBoolean {
        public boolean value;
    }
}
